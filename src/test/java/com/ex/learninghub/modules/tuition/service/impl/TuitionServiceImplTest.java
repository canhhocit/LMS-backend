package com.ex.learninghub.modules.tuition.service.impl;

import com.ex.learninghub.common.email.EmailService;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.tuition.dto.response.PayOSPaymentResponse;
import com.ex.learninghub.modules.tuition.entity.TuitionInvoice;
import com.ex.learninghub.modules.tuition.payos.PayOSService;
import com.ex.learninghub.modules.tuition.repository.TuitionInvoiceRepository;
import com.ex.learninghub.modules.tuition.repository.TuitionRateRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TuitionServiceImplTest {
    @Mock private TuitionRateRepository rateRepository;
    @Mock private TuitionInvoiceRepository invoiceRepository;
    @Mock private UserRepository userRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private EmailService emailService;
    @Mock private NotificationService notificationService;
    @Mock private PayOSService payOSService;
    @InjectMocks private TuitionServiceImpl tuitionService;

    private User student;
    private TuitionInvoice invoice;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        student = User.builder().email("student@test.edu.vn").fullName("Test Student").role(Role.STUDENT).build();
        student.setId(7L);
        principal = new UserPrincipal(student);
        invoice = TuitionInvoice.builder()
                .student(student).semester("2026-FALL").academicYear("2026")
                .totalCredits(3).pricePerCredit(new BigDecimal("100000"))
                .amount(new BigDecimal("300000")).status("UNPAID")
                .payosOrderCode(12345L).payosCheckoutUrl("https://pay.payos.vn/web/test-link")
                .build();
        invoice.setId(12L);
    }

    @Test
    void createPayOSPayment_reusesExistingCheckoutLink() {
        when(invoiceRepository.findById(12L)).thenReturn(Optional.of(invoice));

        PayOSPaymentResponse response = tuitionService.createPayOSPayment(12L, principal);

        assertThat(response.getCheckoutUrl()).isEqualTo("https://pay.payos.vn/web/test-link");
        assertThat(response.getOrderCode()).isEqualTo(12345L);
        verify(payOSService, never()).createPaymentLink(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void verifyPayOSPayment_marksInvoicePaidOnlyAfterProviderConfirms() {
        when(invoiceRepository.findById(12L)).thenReturn(Optional.of(invoice));
        when(payOSService.isPaymentPaid(12345L, invoice.getAmount())).thenReturn(true);
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        var response = tuitionService.verifyPayOSPayment(12L, principal);

        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getPaymentMethod()).isEqualTo("PAYOS");
        verify(emailService).sendTuitionPaymentConfirmation(invoice);
        verify(notificationService).notifyUser(student.getId(), com.ex.learninghub.common.enums.NotificationType.COURSE_REGISTERED,
                "PayOS payment confirmed", "Payment received for tuition invoice 12", 12L);
    }
}
