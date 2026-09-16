package com.ex.learninghub.modules.notification.service;

import com.ex.learninghub.modules.notification.dto.response.NotificationResponse;

public interface RealtimeNotificationService {

    /**
     * Đẩy thông báo thời gian thực đến người dùng qua WebSocket STOMP
     */
    void sendNotificationToUser(String username, NotificationResponse notification);

    /**
     * Phát thông báo chung đến tất cả người dùng trong chủ đề (Topic)
     */
    void broadcastNotification(String topic, NotificationResponse notification);
}
