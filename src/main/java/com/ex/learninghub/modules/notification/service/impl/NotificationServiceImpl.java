package com.ex.learninghub.modules.notification.service.impl;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.notification.dto.response.NotificationResponse;
import com.ex.learninghub.modules.notification.entity.Notification;
import com.ex.learninghub.modules.notification.repository.NotificationRepository;
import com.ex.learninghub.modules.notification.service.NotificationService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import com.ex.learninghub.modules.notification.event.NotificationEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    @Transactional
    public void notifyClazz(Long clazzId, NotificationType type, String title, String content, Long referenceId) {
        List<Long> studentIds = enrollmentRepository.findByClazzId(clazzId).stream()
                .map(e -> e.getStudent().getId())
                .toList();
        for (Long studentId : studentIds) {
            notifyUser(studentId, type, title, content, referenceId);
        }
    }

    @Override
    @Transactional
    public void notifyUser(Long userId, NotificationType type, String title, String content, Long referenceId) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .content(content)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);

        // Publish over WebSocket to the private user queue (keyed by email)
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", notification.getId());
            payload.put("type", type.name());
            payload.put("title", title);
            payload.put("content", content);
            payload.put("referenceId", referenceId);
            messagingTemplate.convertAndSendToUser(
                    recipient.getEmail(), "/queue/notifications", payload);
        } catch (Exception ex) {
            log.warn("Failed to push WebSocket notification to {}: {}", recipient.getEmail(), ex.getMessage());
        }

        // Publish async Email notification event ONLY to personal email (personalEmail)
        try {
            String personalEmail = recipient.getPersonalEmail();
            if (personalEmail != null && !personalEmail.isBlank()) {
                String mailContent = content;
                if (type == NotificationType.NEW_QUIZ) {
                    mailContent = content + "\n\nTruy cập hệ thống để làm bài kiểm tra tại:\n" + frontendUrl + "/student/quizzes";
                } else if (type == NotificationType.NEW_ASSIGNMENT) {
                    mailContent = content + "\n\nTruy cập hệ thống để làm bài tập tại:\n" + frontendUrl + "/student/classes";
                } else {
                    mailContent = content + "\n\nTruy cập hệ thống LearningHub tại:\n" + frontendUrl;
                }

                eventPublisher.publishEvent(NotificationEvent.builder()
                        .recipientEmail(personalEmail.trim())
                        .subject("[" + title + "] Thông báo từ hệ thống LearningHub")
                        .content(mailContent)
                        .notificationType(type.name())
                        .build());
            } else {
                log.info("[Notification] Bỏ qua gửi email cho user ID {} ({}) vì không có email cá nhân (personalEmail)", recipient.getId(), recipient.getEmail());
            }
        } catch (Exception ex) {
            log.warn("Failed to publish email notification event for {}: {}", recipient.getEmail(), ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByRecipientId(userId);
    }
}
