package com.ex.learninghub.modules.notification.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private JavaMailSender mailSender;

    private NotificationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationEventListener(mailSender);
    }

    @Test
    void handleNotificationEvent_sendsMailMessage() {
        NotificationEvent event = NotificationEvent.builder()
                .recipientEmail("student@test.com")
                .subject("Thông báo môn học mới")
                .content("Nội dung thông báo môn học...")
                .notificationType("EMAIL")
                .build();

        listener.handleNotificationEvent(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
