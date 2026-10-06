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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
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

    @Value("${app.payment.simulation-enabled:false}")
    private boolean simulationEnabled;

    // ============== Rates ==============
    @Override
    @Transactional
    public TuitionRateResponse createRate(TuitionRateRequest request) {
        String semester = normalizeSemester(request.getSemester());
        if (rateExists(request.getAcademicYear(), semester, request.getEffectiveFrom(), null)) {
            throw new AppException(ErrorCode.TUITION_RATE_ALREADY_EXISTS);
        }
        TuitionRate r = TuitionRate.builder()
                .academicYear(request.getAcademicYear())
                .semester(semester)
                .effectiveFrom(request.getEffectiveFrom())
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
        String academicYear = request.getAcademicYear();
        String semester = normalizeSemester(request.getSemester());
        LocalDate effectiveFrom = request.getEffectiveFrom();
        if (rateExists(academicYear, semester, effectiveFrom, id)) {
            throw new AppException(ErrorCode.TUITION_RATE_ALREADY_EXISTS);
        }
        r.setAcademicYear(academicYear);
        r.setSemester(semester);
        r.setEffectiveFrom(effectiveFrom);
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

        LocalDate rateDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        TuitionRate rate = rateRepository
                .findFirstByAcademicYearAndSemesterAndEffectiveFromLessThanEqualAndIsActiveTrueOrderByEffectiveFromDesc(
                        academicYear, normalizeSemester(semester), rateDate)
                .or(() -> rateRepository
                        .findFirstByAcademicYearAndSemesterIsNullAndEffectiveFromLessThanEqualAndIsActiveTrueOrderByEffectiveFromDesc(
                                academicYear, rateDate))
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

    private boolean rateExists(String academicYear, String semester, LocalDate effectiveFrom, Long excludedId) {
        if (semester == null) {
            return excludedId == null
                    ? rateRepository.existsByAcademicYearAndSemesterIsNullAndEffectiveFrom(academicYear, effectiveFrom)
                    : rateRepository.existsByAcademicYearAndSemesterIsNullAndEffectiveFromAndIdNot(
                            academicYear, effectiveFrom, excludedId);
        }
        return excludedId == null
                ? rateRepository.existsByAcademicYearAndSemesterAndEffectiveFrom(academicYear, semester, effectiveFrom)
                : rateRepository.existsByAcademicYearAndSemesterAndEffectiveFromAndIdNot(
                        academicYear, semester, effectiveFrom, excludedId);
    }

    private String normalizeSemester(String semester) {
        if (semester == null || semester.isBlank()) return null;
        return semester.trim().toUpperCase();
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse simulatePayment(Long invoiceId, UserPrincipal principal) {
        if (!simulationEnabled) throw new AppException(ErrorCode.FORBIDDEN);
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if ("PAID".equals(inv.getStatus())) {
            return TuitionInvoiceResponse.from(inv);
        }
        if (inv.getPayosOrderCode() != null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        inv.setStatus("PAID");
        inv.setPaymentMethod("SIMULATED");
        inv.setPaidAt(java.time.LocalDateTime.now());
        TuitionInvoice saved = invoiceRepository.save(inv);

        notificationService.notifyUser(saved.getStudent().getId(), NotificationType.COURSE_REGISTERED,
                "Simulated payment recorded", "Invoice " + saved.getId() + " was marked paid in simulation mode only.", saved.getId());

        return TuitionInvoiceResponse.from(saved);
    }

    // ============== PayOS Payment Integration ==============
    @Override
    @Transactional
    public PayOSPaymentResponse createPayOSPayment(Long invoiceId, UserPrincipal principal) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if ("PAID".equals(inv.getStatus())) throw new AppException(ErrorCode.VALIDATION_ERROR);
        if (inv.getPayosOrderCode() != null) {
            if (inv.getPayosCheckoutUrl() == null || inv.getPayosCheckoutUrl().isBlank()) {
                PayOSPaymentResponse restored = payOSService.getPendingPaymentLink(
                        invoiceId, inv.getPayosOrderCode(), inv.getAmount());
                inv.setPayosCheckoutUrl(restored.getCheckoutUrl());
                inv.setPayosQrCode(restored.getQrCode());
                inv.setPayosAccountName(restored.getAccountName());
                inv.setPayosAccountNumber(restored.getAccountNumber());
                inv.setPayosBankName(restored.getBankName());
                inv.setPayosDescription(restored.getDescription());
                invoiceRepository.save(inv);
                return restored;
            }
            return getPaymentResponse(inv);
        }
        String description = "Hoc phi K" + inv.getSemester() + " " + inv.getAcademicYear();
        PayOSPaymentResponse response = payOSService.createPaymentLink(invoiceId, inv.getAmount(), description, inv.getStudent().getFullName());
        inv.setPayosOrderCode(response.getOrderCode());
        inv.setPayosCheckoutUrl(response.getCheckoutUrl());
        inv.setPayosQrCode(response.getQrCode());
        inv.setPayosAccountName(response.getAccountName());
        inv.setPayosAccountNumber(response.getAccountNumber());
        inv.setPayosBankName(response.getBankName());
        inv.setPayosDescription(response.getDescription());
        invoiceRepository.save(inv);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PayOSPaymentResponse getPendingPayOSPayment(Long invoiceId, UserPrincipal principal) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (inv.getPayosOrderCode() == null || inv.getPayosCheckoutUrl() == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        return getPaymentResponse(inv);
    }

    private PayOSPaymentResponse getPaymentResponse(TuitionInvoice inv) {
        return PayOSPaymentResponse.builder()
                .invoiceId(inv.getId())
                .orderCode(inv.getPayosOrderCode())
                .amount(inv.getAmount())
                .checkoutUrl(inv.getPayosCheckoutUrl())
                .qrCode(inv.getPayosQrCode())
                .accountName(inv.getPayosAccountName())
                .accountNumber(inv.getPayosAccountNumber())
                .bankName(inv.getPayosBankName())
                .description(inv.getPayosDescription())
                .status(inv.getStatus().equals("PAID") ? "PAID" : "PENDING")
                .build();
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse verifyPayOSPayment(Long invoiceId, UserPrincipal principal) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (!inv.getStudent().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (!"PAID".equals(inv.getStatus()) && inv.getPayosOrderCode() != null
                && payOSService.isPaymentPaid(inv.getPayosOrderCode(), inv.getAmount())) {
            inv.setStatus("PAID");
            inv.setPaymentMethod("PAYOS");
            inv.setPaidAt(LocalDateTime.now());
            TuitionInvoice saved = invoiceRepository.save(inv);
            emailService.sendTuitionPaymentConfirmation(saved);
            notificationService.notifyUser(saved.getStudent().getId(), NotificationType.COURSE_REGISTERED,
                    "PayOS payment confirmed", "Payment received for tuition invoice " + saved.getId(), saved.getId());
        }
        return TuitionInvoiceResponse.from(inv);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public TuitionInvoiceResponse processPayOSWebhook(Map<String, Object> payload) {
        if (payload == null || !(payload.get("data") instanceof Map<?, ?> rawData)
                || !payOSService.verifyWebhookData((Map<String, Object>) rawData, String.valueOf(payload.get("signature")))) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        Map<String, Object> data = (Map<String, Object>) rawData;
        if (!Boolean.TRUE.equals(payload.get("success")) || !"00".equals(String.valueOf(payload.get("code")))
                || data.get("orderCode") == null || data.get("amount") == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        Long orderCode = Long.valueOf(data.get("orderCode").toString());
        long amount = Long.parseLong(data.get("amount").toString());
        TuitionInvoice inv = invoiceRepository.findByPayosOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if (inv.getAmount().compareTo(BigDecimal.valueOf(amount)) != 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        if ("PAID".equals(inv.getStatus())) return TuitionInvoiceResponse.from(inv);

        inv.setStatus("PAID");
        inv.setPaymentMethod("PAYOS");
        inv.setPaidAt(LocalDateTime.now());
        TuitionInvoice saved = invoiceRepository.save(inv);
        emailService.sendTuitionPaymentConfirmation(saved);
        notificationService.notifyUser(saved.getStudent().getId(), NotificationType.COURSE_REGISTERED,
                "Xác nhận thanh toán PayOS", "Đã nhận thanh toán PayOS cho hóa đơn học phí kỳ "
                        + saved.getSemester() + " năm " + saved.getAcademicYear(), saved.getId());
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
        inv.setPaymentMethod("MANUAL");
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
