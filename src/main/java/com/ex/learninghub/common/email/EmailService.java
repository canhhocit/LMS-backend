package com.ex.learninghub.common.email;

import com.ex.learninghub.modules.tuition.entity.TuitionInvoice;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service gửi email HTML không trả lời (no-reply) cho các sự kiện quan trọng.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@learninghub.edu.vn}")
    private String fromAddress;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final NumberFormat VND =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // ===================================================================
    //  Course Registration: gửi email xác nhận đăng ký học phần
    // ===================================================================

    @Async("taskExecutor")
    public void sendCourseRegistrationEmail(com.ex.learninghub.modules.user.entity.User student,
                                            com.ex.learninghub.modules.course.entity.Clazz clazz,
                                            com.ex.learninghub.modules.registration.entity.RegistrationPeriod period) {
        if (student == null) return;
        
        // Ưu tiên gửi về email cá nhân (personalEmail) do email mặc định có thể là giả
        String to = (student.getPersonalEmail() != null && !student.getPersonalEmail().isBlank())
                ? student.getPersonalEmail()
                : student.getEmail();

        if (to == null || to.isBlank()) {
            log.warn("[Email] Sinh viên {} không có email cá nhân hay email tổ chức – bỏ qua gửi xác nhận đăng ký",
                    student.getFullName());
            return;
        }

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAddress, "LearningHub – Phòng Đào tạo [no-reply]");
            helper.setTo(to);
            helper.setSubject("[LearningHub] Xác nhận đăng ký thành công học phần – " + clazz.getClassName());
            
            String htmlContent = buildCourseRegistrationHtml(student, clazz, period);
            helper.setText(htmlContent, true);
            mailSender.send(msg);
            log.info("[Email] Đã gửi xác nhận đăng ký học phần tới {} (clazzId={})", to, clazz.getId());
        } catch (Exception e) {
            log.warn("[Email] Gửi mail xác nhận đăng ký học phần thất bại: {}", e.getMessage());
        }
    }

    // ===================================================================
    //  Tuition: gửi email xác nhận thanh toán
    // ===================================================================

    @Async("taskExecutor")
    public void sendTuitionPaymentConfirmation(TuitionInvoice invoice) {
        String to = resolveEmail(invoice);
        if (to == null) {
            log.warn("[Email] Sinh viên {} không có email – bỏ qua gửi xác nhận học phí",
                    invoice.getStudent() != null ? invoice.getStudent().getFullName() : "?");
            return;
        }

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAddress, "LearningHub – Phòng Tài chính [no-reply]");
            helper.setTo(to);
            helper.setSubject("[LearningHub] Xác nhận thanh toán học phí – Mã HĐ #TUITION-" + invoice.getId());
            helper.setText(buildPaymentConfirmationHtml(invoice), true); // true = HTML
            mailSender.send(msg);
            log.info("[Email] Đã gửi xác nhận thanh toán học phí tới {} (invoiceId={})", to, invoice.getId());
        } catch (Exception e) {
            log.warn("[Email] Gửi mail xác nhận học phí thất bại (có thể do môi trường dev/offline): {}", e.getMessage());
        }
    }

    // ===================================================================
    //  Private helpers
    // ===================================================================

    /** Ưu tiên email cá nhân (personalEmail) của sinh viên, fallback về email tổ chức. */
    private String resolveEmail(TuitionInvoice invoice) {
        if (invoice.getStudent() == null) return null;
        var student = invoice.getStudent();
        if (student.getPersonalEmail() != null && !student.getPersonalEmail().isBlank()) {
            return student.getPersonalEmail();
        }
        if (student.getEmail() != null && !student.getEmail().isBlank()) {
            return student.getEmail();
        }
        return null;
    }

    private String buildPaymentConfirmationHtml(TuitionInvoice inv) {
        String studentName = inv.getStudent() != null ? inv.getStudent().getFullName() : "Sinh viên";
        String studentId   = inv.getStudent() != null && inv.getStudent().getStudentCode() != null
                ? inv.getStudent().getStudentCode() : "—";
        String paidAtStr   = inv.getPaidAt() != null ? inv.getPaidAt().format(DATE_FMT) : "—";
        String dueDateStr  = inv.getDueDate() != null ? inv.getDueDate().format(DATE_FMT) : "—";
        String amountStr   = VND.format(inv.getAmount() != null ? inv.getAmount() : BigDecimal.ZERO);
        String ppCreditStr = VND.format(inv.getPricePerCredit() != null ? inv.getPricePerCredit() : BigDecimal.ZERO);
        int credits        = inv.getTotalCredits() != null ? inv.getTotalCredits() : 0;
        String invoiceId   = "#TUITION-" + inv.getId();
        String semester    = inv.getSemester() != null ? inv.getSemester() : "—";
        String acadYear    = inv.getAcademicYear() != null ? inv.getAcademicYear() : "—";

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Xác nhận thanh toán học phí</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f1f5f9;font-family:Inter,'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background:#f1f5f9;padding:40px 20px;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%%;">

                        <!-- HEADER -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#4f46e5 0%%,#2563eb 100%%);border-radius:16px 16px 0 0;padding:36px 40px;text-align:center;">
                            <div style="display:inline-block;background:rgba(255,255,255,0.15);border-radius:12px;padding:10px 18px;margin-bottom:16px;">
                              <span style="color:#fff;font-size:20px;font-weight:800;letter-spacing:0.5px;vertical-align:middle;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display:inline-block;vertical-align:middle;margin-right:8px;">
                                  <path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/>
                                </svg>
                                LearningHub
                              </span>
                            </div>
                            <h1 style="color:#ffffff;margin:0;font-size:22px;font-weight:700;">Xác nhận thanh toán học phí</h1>
                            <p style="color:#c7d2fe;margin:8px 0 0;font-size:14px;">Giao dịch đã được ghi nhận thành công</p>
                          </td>
                        </tr>

                        <!-- SUCCESS BADGE -->
                        <tr>
                          <td style="background:#ffffff;padding:28px 40px 20px;text-align:center;">
                            <div style="display:inline-table;align-items:center;background:#ecfdf5;border:1px solid #a7f3d0;border-radius:999px;padding:10px 22px;">
                              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#059669" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="display:inline-block;vertical-align:middle;margin-right:8px;">
                                <polyline points="20 6 9 17 4 12"/>
                              </svg>
                              <span style="color:#065f46;font-weight:700;font-size:15px;vertical-align:middle;">Thanh toán thành công</span>
                            </div>
                          </td>
                        </tr>

                        <!-- STUDENT INFO -->
                        <tr>
                          <td style="background:#ffffff;padding:0 40px 20px;">
                            <p style="margin:0 0 4px;color:#64748b;font-size:13px;">Kính gửi,</p>
                            <p style="margin:0;color:#1e293b;font-size:16px;font-weight:700;">%s</p>
                            <p style="margin:4px 0 0;color:#64748b;font-size:13px;">Mã sinh viên: <strong>%s</strong></p>
                          </td>
                        </tr>

                        <!-- INVOICE CARD -->
                        <tr>
                          <td style="background:#ffffff;padding:0 40px 28px;">
                            <div style="border:1px solid #e2e8f0;border-radius:12px;overflow:hidden;">
                              <div style="background:#f8fafc;border-bottom:1px solid #e2e8f0;padding:14px 20px;display:flex;justify-content:space-between;align-items:center;">
                                <span style="font-weight:700;color:#334155;font-size:14px;">Hóa đơn học phí</span>
                                <span style="font-size:13px;color:#6366f1;font-weight:600;">%s</span>
                              </div>
                              <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="border-collapse:collapse;">
                                %s
                              </table>
                            </div>
                          </td>
                        </tr>

                        <!-- TOTAL AMOUNT -->
                        <tr>
                          <td style="background:#ffffff;padding:0 40px 28px;">
                            <div style="background:linear-gradient(135deg,#4f46e5 0%%,#2563eb 100%%);border-radius:12px;padding:20px 24px;display:flex;justify-content:space-between;align-items:center;">
                              <span style="color:#c7d2fe;font-size:14px;font-weight:600;">Tổng số tiền đã thanh toán:</span>
                              <span style="color:#ffffff;font-size:22px;font-weight:800;">%s</span>
                            </div>
                          </td>
                        </tr>

                        <!-- FOOTER NOTE -->
                        <tr>
                          <td style="background:#ffffff;padding:0 40px 28px;">
                            <div style="background:#fef3c7;border:1px solid #fde68a;border-radius:10px;padding:14px 18px;">
                              <p style="margin:0;color:#78350f;font-size:13px;line-height:1.6;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#92400e" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display:inline-block;vertical-align:middle;margin-right:6px;">
                                  <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                                </svg>
                                <strong>Lưu ý:</strong> Email này được gửi tự động từ hệ thống LearningHub. Vui lòng không trả lời email này.
                                Nếu bạn có thắc mắc về hóa đơn, hãy liên hệ Phòng Tài chính – Kế toán của trường hoặc đăng nhập vào
                                cổng thông tin sinh viên để kiểm tra.
                              </p>
                            </div>
                          </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                          <td style="background:#1e293b;border-radius:0 0 16px 16px;padding:24px 40px;text-align:center;">
                            <p style="color:#94a3b8;font-size:12px;margin:0 0 4px;">© 2026 LearningHub – Hệ thống quản lý học tập</p>
                            <p style="color:#475569;font-size:11px;margin:0;">Email tự động – no-reply@learninghub.edu.vn</p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(
                studentName, studentId,
                invoiceId,
                buildInvoiceRows(semester, acadYear, credits, ppCreditStr, paidAtStr, dueDateStr),
                amountStr
        );
    }

    private String buildCourseRegistrationHtml(com.ex.learninghub.modules.user.entity.User student,
                                               com.ex.learninghub.modules.course.entity.Clazz clazz,
                                               com.ex.learninghub.modules.registration.entity.RegistrationPeriod period) {
        String studentName = student != null ? student.getFullName() : "Sinh viên";
        String studentId   = student != null && student.getStudentCode() != null ? student.getStudentCode() : "—";
        String className   = clazz != null ? clazz.getClassName() : "—";
        String classCode   = clazz != null ? clazz.getClassCode() : "—";
        String courseName  = (clazz != null && clazz.getCourse() != null) ? clazz.getCourse().getTitle() : "—";
        int credits        = (clazz != null && clazz.getCourse() != null && clazz.getCourse().getCredit() != null)
                ? clazz.getCourse().getCredit() : 0;
        String semester    = period != null && period.getSemester() != null ? period.getSemester() : "—";
        String acadYear    = period != null && period.getAcademicYear() != null ? period.getAcademicYear() : "—";
        String closeAtStr  = period != null && period.getCloseAt() != null ? period.getCloseAt().format(DATE_FMT) : "—";

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                  <meta charset="UTF-8"/>
                  <title>Xác nhận đăng ký học phần</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f1f5f9;font-family:Inter,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background:#f1f5f9;padding:40px 20px;">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" border="0" style="max-width:600px;width:100%%;">
                        <tr>
                          <td style="background:linear-gradient(135deg,#4f46e5 0%%,#2563eb 100%%);border-radius:16px 16px 0 0;padding:36px 40px;text-align:center;">
                            <h1 style="color:#ffffff;margin:0;font-size:22px;font-weight:700;">Xác nhận Đăng ký Học phần</h1>
                            <p style="color:#c7d2fe;margin:8px 0 0;font-size:14px;">LearningHub – Hệ thống Quản lý Đào tạo</p>
                          </td>
                        </tr>
                        <tr>
                          <td style="background:#ffffff;padding:24px 40px;">
                            <p style="margin:0 0 4px;color:#64748b;font-size:13px;">Kính gửi sinh viên,</p>
                            <p style="margin:0;color:#1e293b;font-size:16px;font-weight:700;">%s (Mã SV: %s)</p>
                            <p style="margin:12px 0 0;color:#334155;font-size:14px;line-height:1.6;">
                              Bạn đã đăng ký thành công lớp học phần <strong>%s</strong> (%s) thuộc môn học <strong>%s</strong> (%d tín chỉ).
                            </p>
                          </td>
                        </tr>
                        <tr>
                          <td style="background:#ffffff;padding:0 40px 24px;">
                            <div style="border:1px solid #e2e8f0;border-radius:12px;overflow:hidden;">
                              <div style="background:#f8fafc;padding:12px 20px;font-weight:700;color:#334155;">Chi tiết lớp học phần</div>
                              <table width="100%%" cellpadding="0" cellspacing="0" border="0">
                                %s
                              </table>
                            </div>
                          </td>
                        </tr>
                        <tr>
                          <td style="background:#ffffff;padding:0 40px 28px;">
                            <div style="background:#fef3c7;border:1px solid #fde68a;border-radius:10px;padding:16px 20px;">
                              <p style="margin:0;color:#78350f;font-size:13px;line-height:1.6;">
                                <strong>⚠️ Lưu ý Hạn nộp Học phí:</strong> Hóa đơn học phí tự động đã được khởi tạo cho học kỳ %s (%s). Vui lòng hoàn thành nộp học phí trước hạn chót đợt đăng ký: <strong>%s</strong>.
                              </p>
                            </div>
                          </td>
                        </tr>
                        <tr>
                          <td style="background:#1e293b;border-radius:0 0 16px 16px;padding:20px 40px;text-align:center;">
                            <p style="color:#94a3b8;font-size:12px;margin:0;">© 2026 LearningHub – Email tự động no-reply</p>
                          </td>
                        </tr>
                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(
                studentName, studentId, className, classCode, courseName, credits,
                buildRow("Mã lớp", classCode, false) +
                buildRow("Tên lớp", className, true) +
                buildRow("Môn học", courseName, false) +
                buildRow("Số tín chỉ", credits + " tín chỉ", true) +
                buildRow("Học kỳ", semester, false) +
                buildRow("Năm học", acadYear, true),
                semester, acadYear, closeAtStr
        );
    }

    private static String buildInvoiceRows(String semester, String acadYear, int credits,
                                           String ppCredit, String paidAt, String dueDate) {
        return buildRow("Học kỳ", semester, false)
             + buildRow("Năm học", acadYear, true)
             + buildRow("Số tín chỉ", credits + " tín chỉ", false)
             + buildRow("Đơn giá / tín chỉ", ppCredit, true)
             + buildRow("Thời gian thanh toán", paidAt, false)
             + buildRow("Hạn thanh toán gốc", dueDate, true);
    }

    private static String buildRow(String label, String value, boolean shaded) {
        String bg = shaded ? "#f8fafc" : "#ffffff";
        return "<tr>"
             + "<td style=\"padding:11px 20px;color:#64748b;font-size:13px;background:" + bg + ";border-bottom:1px solid #f1f5f9;\">" + label + ":</td>"
             + "<td style=\"padding:11px 20px;color:#1e293b;font-size:13px;font-weight:600;background:" + bg + ";border-bottom:1px solid #f1f5f9;text-align:right;\">" + value + "</td>"
             + "</tr>";
    }
}
