package com.ex.learninghub.modules.notification.service.impl;

import com.ex.learninghub.modules.notification.dto.response.NotificationResponse;
import com.ex.learninghub.modules.notification.service.RealtimeNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtimeNotificationServiceImpl implements RealtimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotificationToUser(String username, NotificationResponse notification) {
        if (username == null || username.isBlank()) return;
        try {
            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
            log.info("[WebSocket-STOMP] Đã đẩy thông báo real-time tới người dùng '{}': {}", username, notification.getTitle());
        } catch (Exception e) {
            log.warn("[WebSocket-STOMP] Đẩy thông báo real-time thất bại: {}", e.getMessage());
        }
    }

    @Override
    public void broadcastNotification(String topic, NotificationResponse notification) {
        try {
            String destination = topic.startsWith("/") ? topic : "/topic/" + topic;
            messagingTemplate.convertAndSend(destination, notification);
            log.info("[WebSocket-STOMP] Phát thông báo Broadcast tới topic '{}': {}", destination, notification.getTitle());
        } catch (Exception e) {
            log.warn("[WebSocket-STOMP] Broadcast thông báo thất bại: {}", e.getMessage());
        }
    }
}
