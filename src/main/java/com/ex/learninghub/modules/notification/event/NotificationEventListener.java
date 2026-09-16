package com.ex.learninghub.modules.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("[Async-Event] Đang xử lý bất đồng bộ event '{}' gửi đến email '{}' trên luồng [{}]",
                event.getSubject(), event.getRecipientEmail(), Thread.currentThread().getName());

        if (event.getRecipientEmail() != null && !event.getRecipientEmail().isBlank()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(event.getRecipientEmail());
                message.setSubject(event.getSubject());
                message.setText(event.getContent());
                mailSender.send(message);
                log.info("[Async-Event] Đã gửi Mail ngầm thành công tới {}", event.getRecipientEmail());
            } catch (Exception e) {
                log.warn("[Async-Event] Gửi Mail ngầm thất bại (môi trường Dev/Offline): {}", e.getMessage());
            }
        }
    }
}
