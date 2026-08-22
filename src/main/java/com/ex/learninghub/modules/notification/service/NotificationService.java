package com.ex.learninghub.modules.notification.service;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.modules.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void notifyClazz(Long clazzId, NotificationType type, String title, String content, Long referenceId);

    void notifyUser(Long userId, NotificationType type, String title, String content, Long referenceId);

    Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable);

    long countUnread(Long userId);

    void markAsRead(Long notificationId, Long userId);
}
