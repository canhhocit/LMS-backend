package com.ex.learninghub.modules.notification.service.impl;

import com.ex.learninghub.common.enums.NotificationType;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.modules.notification.entity.Notification;
import com.ex.learninghub.modules.notification.repository.NotificationRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User recipient;

    @BeforeEach
    void setUp() {
        recipient = User.builder().email("sv@test.edu.vn").role(Role.STUDENT).build();
        recipient.setId(1L);
    }

    @Test
    void notifyUser_savesToDb_andPushesToWebSocket() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(recipient));
        when(notificationRepository.save(any())).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(99L);
            return n;
        });

        notificationService.notifyUser(1L, NotificationType.NEW_GRADE, "Grade updated", "Your midterm is 8.5", 50L);

        // 1) DB save
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        Notification saved = captor.getValue();
        assertThat(saved.getRecipient().getId()).isEqualTo(1L);
        assertThat(saved.getType()).isEqualTo(NotificationType.NEW_GRADE);
        assertThat(saved.getTitle()).isEqualTo("Grade updated");
        assertThat(saved.getContent()).isEqualTo("Your midterm is 8.5");
        assertThat(saved.getReferenceId()).isEqualTo(50L);

        // 2) WebSocket push to user queue
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                eq("sv@test.edu.vn"), eq("/queue/notifications"), any(Object.class));
    }

    @Test
    void notifyUser_throwsUserNotFound_whenRecipientMissing() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.notifyUser(404L, NotificationType.NEW_GRADE, "t", "c", 1L))
                .isInstanceOf(AppException.class);

        verify(notificationRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any(Object.class));
    }

    @Test
    void markAsRead_throwsForbidden_whenOtherUserTries() {
        Notification n = Notification.builder()
                .recipient(recipient)
                .type(NotificationType.NEW_ANNOUNCEMENT)
                .title("t")
                .content("c")
                .isRead(false)
                .build();
        n.setId(11L);

        when(notificationRepository.findById(11L)).thenReturn(Optional.of(n));

        // Recipient id 1; principal id 2 → not owner
        User other = User.builder().email("x@y").role(Role.STUDENT).build();
        other.setId(2L);

        assertThatThrownBy(() -> notificationService.markAsRead(11L, 2L))
                .isInstanceOf(AppException.class);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAsRead_succeeds_forOwner() {
        Notification n = Notification.builder()
                .recipient(recipient)
                .type(NotificationType.NEW_ANNOUNCEMENT)
                .title("t")
                .content("c")
                .isRead(false)
                .build();
        n.setId(11L);

        when(notificationRepository.findById(11L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationService.markAsRead(11L, 1L);

        assertThat(n.getIsRead()).isTrue();
        verify(notificationRepository, times(1)).save(any());
    }
}
