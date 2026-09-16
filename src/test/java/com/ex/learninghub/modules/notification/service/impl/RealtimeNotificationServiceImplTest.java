package com.ex.learninghub.modules.notification.service.impl;

import com.ex.learninghub.modules.notification.dto.response.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RealtimeNotificationServiceImplTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private RealtimeNotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RealtimeNotificationServiceImpl(messagingTemplate);
    }

    @Test
    void sendNotificationToUser_sendsMessageToUserQueue() {
        NotificationResponse notification = NotificationResponse.builder()
                .id(1L)
                .title("Thông báo học tập")
                .content("Lớp học được dời lịch sang sáng thứ 5")
                .build();

        service.sendNotificationToUser("student@test.com", notification);

        verify(messagingTemplate).convertAndSendToUser("student@test.com", "/queue/notifications", notification);
    }
}
