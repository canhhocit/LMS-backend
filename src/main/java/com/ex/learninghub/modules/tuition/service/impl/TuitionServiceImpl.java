package com.ex.learninghub.modules.tuition.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.tuition.dto.request.TuitionRateRequest;
import com.ex.learninghub.modules.tuition.dto.response.TuitionInvoiceResponse;
import com.ex.learninghub.modules.tuition.dto.response.TuitionRateResponse;
import com.ex.learninghub.modules.tuition.entity.TuitionInvoice;
import com.ex.learninghub.modules.tuition.entity.TuitionRate;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TuitionServiceImpl implements TuitionService {

    private final TuitionRateRepository rateRepository;
    private final TuitionInvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

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
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (student.getRole() != Role.STUDENT) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        // Đã có invoice cho (student, semester, year) → không tạo lại
        var existing = invoiceRepository.findByStudentIdAndSemesterAndAcademicYear(studentId, semester, academicYear);
        if (existing.isPresent()) {
            return TuitionInvoiceResponse.from(existing.get());
        }

        TuitionRate rate = rateRepository.findByAcademicYear(academicYear)
                .orElseThrow(() -> new AppException(ErrorCode.TUITION_RATE_NOT_FOUND));

        // Tính tổng tín chỉ của sinh viên trong kỳ này (theo enrollments chưa xóa).
        // Vì model không có semester trên enrollment, lấy tất cả enrollments hiện tại
        // và tính tổng credit các course đang enroll.
        int totalCredits = enrollmentRepository.findByStudentId(studentId).stream()
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
                .build();
        return TuitionInvoiceResponse.from(invoiceRepository.save(inv));
    }

    @Override
    @Transactional
    public TuitionInvoiceResponse markPaid(Long invoiceId) {
        TuitionInvoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        if ("PAID".equals(inv.getStatus())) {
            return TuitionInvoiceResponse.from(inv); // idempotent
        }
        inv.setStatus("PAID");
        inv.setPaidAt(LocalDateTime.now());
        return TuitionInvoiceResponse.from(invoiceRepository.save(inv));
    }
}
