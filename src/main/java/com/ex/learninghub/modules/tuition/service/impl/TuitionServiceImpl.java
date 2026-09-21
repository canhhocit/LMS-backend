package com.ex.learninghub.modules.tuition.service.impl;

import com.ex.learninghub.common.email.EmailService;
import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.PayOSPaymentResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionInvoiceResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionRateResponse;
import com.ex.learninghub.modules.tuition.entity.TuitionInvoice;
import com.ex.learninghub.modules.tuition.entity.TuitionRate;
import com.ex.learninghub.modules.tuition.payos.PayOSService;
import com.ex.learninghub.modules.tuition.repository.TuitionInvoiceRepository;
import com.ex.learninghub.modules.tuition.repository.TuitionRateRepository;
import com.ex.learninghub.modules.tuition.service.TuitionService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TuitionServiceImpl implements TuitionService {

    private final TuitionRateRepository rateRepository;
    private final TuitionInvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final PayOSService payOSService;

    // ============== Rates ==============
    @Override
    @Transactional
    public TuitionRateResponse createRate(TuitionRateRequest request) {
        if (rateRepository.existsByAcademicYear(request.getAcademicYear())) {
            throw new AppException(ErrorCode.TUITION_RATE_ALREADY_EXISTS);
        }
        TuitionRate r = TuitionRate.builder()
                .academicYear(request.getAcademicYear())
                .pricePerCredit(request.getPricePerCredit())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .build();
        return TuitionRateResponse.from(rateRepository.save(r));
    }

    @Override
    @Transactional
    public TuitionRateResponse updateRate(Long id, TuitionRateRequest request) {
        TuitionRate r = rateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TUITION_RATE_NOT_FOUND));
        r.setPricePerCredit(request.getPricePerCredit());
        if (request.getIsActive() != null) r.setIsActive(request.getIsActive());
        return TuitionRateResponse.from(rateRepository.save(r));
    }

    @Override
    @Transactional
    public void deleteRate(Long id) {
        rateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TuitionRateResponse> listRates() {
        return rateRepository.findAll().stream()
                .map(TuitionRateResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TuitionRateResponse getRate(Long id) {
        return TuitionRateResponse.from(rateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TUITION_RATE_NOT_FOUND)));
    }

    // ============== Invoices ==============
    @Override
    @Transactional(readOnly = true)
    public List<TuitionInvoiceResponse> getMyInvoices(UserPrincipal principal) {
        return invoiceRepository.findByStudentId(principal.getUser().getId()).stream()
                .map(TuitionInvoiceResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse generateInvoice(Long studentId, String semester, String academicYear) {
        if (semester == null || semester.isBlank() || academicYear == null || academicYear.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (student.getRole() != Role.STUDENT) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        var existing = invoiceRepository.findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);
        if (existing.isPresent()) {
            return TuitionInvoiceResponse.from(existing.get());
        }

        TuitionRate rate = rateRepository.findByAcademicYear(academicYear)
                .orElseThrow(() -> new AppException(ErrorCode.TUITION_RATE_NOT_FOUND));

        int totalCredits = enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> semester.equals(e.getSemester()) && academicYear.equals(e.getAcademicYear()))
                .filter(e -> e.getClazz() != null && e.getClazz().getCourse() != null
                        && e.getClazz().getCourse().getCredit() != null)
                .mapToInt(e -> e.getClazz().getCourse().getCredit())
                .sum();

        BigDecimal amount = rate.getPricePerCredit()
                .multiply(BigDecimal.valueOf(totalCredits))
                .setScale(2, RoundingMode.HALF_UP);

        TuitionInvoice inv = TuitionInvoice.builder()
                .student(student)
                .semester(semester)
                .academicYear(academicYear)
                .totalCredits(totalCredits)
                .pricePerCredit(rate.getPricePerCredit())
                .amount(amount)
                .status("UNPAID")
                .dueDate(LocalDateTime.now().plusDays(30))
                .build();
        return TuitionInvoiceResponse.from(invoiceRepository.save(inv));
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse payMyInvoice(Long invoiceId, UserPrincipal principal) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if ("PAID".equals(inv.getStatus())) {
            return TuitionInvoiceResponse.from(inv);
        }
        inv.setStatus("PAID");
        inv.setPaidAt(java.time.LocalDateTime.now());
        TuitionInvoice saved = invoiceRepository.save(inv);

        emailService.sendTuitionPaymentConfirmation(saved);

        notificationService.notifyUser(
                saved.getStudent().getId(),
                NotificationType.COURSE_REGISTERED,
                "Đã thanh toán học phí thành công",
                "Hóa đơn học phí kỳ " + saved.getSemester() + " năm " + saved.getAcademicYear()
                        + " đã được ghi nhận. Kiểm tra email cá nhân để xem hóa đơn.",
                saved.getId());

        return TuitionInvoiceResponse.from(saved);
    }

    // ============== PayOS Payment Integration ==============
    @Override
    @Transactional(readOnly = true)
    public PayOSPaymentResponse createPayOSPayment(Long invoiceId, UserPrincipal principal) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        String description = "Hoc phi K" + inv.getSemester() + " " + inv.getAcademicYear();
        return payOSService.createPaymentLink(invoiceId, inv.getAmount(), description, inv.getStudent().getFullName());
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse verifyPayOSPayment(Long invoiceId, UserPrincipal principal) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if ("PAID".equals(inv.getStatus())) {
            return TuitionInvoiceResponse.from(inv);
        }

        inv.setStatus("PAID");
        inv.setPaidAt(LocalDateTime.now());
        TuitionInvoice saved = invoiceRepository.save(inv);

        emailService.sendTuitionPaymentConfirmation(saved);
        notificationService.notifyUser(
                saved.getStudent().getId(),
                NotificationType.COURSE_REGISTERED,
                "Thanh toán PayOS thành công",
                "Hóa đơn học phí kỳ " + saved.getSemester() + " năm " + saved.getAcademicYear()
                        + " đã được thanh toán thành công qua cổng PayOS VietQR.",
                saved.getId());

        return TuitionInvoiceResponse.from(saved);
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse processPayOSWebhook(Map<String, Object> payload) {
        if (payload == null || !payload.containsKey("data")) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Object orderCodeObj = data.get("orderCode");
        Object descObj = data.get("description");

        Long invoiceId = null;
        if (descObj != null) {
            String desc = descObj.toString();
            String[] parts = desc.split(" ");
            for (String part : parts) {
                try {
                    invoiceId = Long.parseLong(part);
                    break;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (invoiceId == null) {
            List<TuitionInvoice> unpaid = invoiceRepository.findAll().stream()
                    .filter(i -> "UNPAID".equals(i.getStatus()))
                    .collect(Collectors.toList());
            if (!unpaid.isEmpty()) {
                invoiceId = unpaid.get(0).getId();
            }
        }

        if (invoiceId == null) {
            throw new AppException(ErrorCode.SUBMISSION_NOT_FOUND);
        }

        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));

        if ("PAID".equals(inv.getStatus())) {
            return TuitionInvoiceResponse.from(inv);
        }

        inv.setStatus("PAID");
        inv.setPaidAt(LocalDateTime.now());
        TuitionInvoice saved = invoiceRepository.save(inv);

        emailService.sendTuitionPaymentConfirmation(saved);
        if (saved.getStudent() != null) {
            notificationService.notifyUser(
                    saved.getStudent().getId(),
                    NotificationType.COURSE_REGISTERED,
                    "Xác nhận thanh toán PayOS VietQR",
                    "Hệ thống đã nhận thanh toán thành công qua PayOS cho hóa đơn học phí kỳ "
                            + saved.getSemester() + " năm " + saved.getAcademicYear(),
                    saved.getId());
        }

        return TuitionInvoiceResponse.from(saved);
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse markPaid(Long invoiceId) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if ("PAID".equals(inv.getStatus())) {
            return TuitionInvoiceResponse.from(inv);
        }
        inv.setStatus("PAID");
        inv.setPaidAt(java.time.LocalDateTime.now());
        TuitionInvoice saved = invoiceRepository.save(inv);

        emailService.sendTuitionPaymentConfirmation(saved);

        if (saved.getStudent() != null) {
            notificationService.notifyUser(
                    saved.getStudent().getId(),
                    NotificationType.COURSE_REGISTERED,
                    "Học phí đã được xác nhận",
                    "Hóa đơn học phí kỳ " + saved.getSemester() + " năm " + saved.getAcademicYear()
                            + " đã được phòng Tài chính xác nhận. Kiểm tra email để xem hóa đơn.",
                    saved.getId());
        }

        return TuitionInvoiceResponse.from(saved);
    }
}
