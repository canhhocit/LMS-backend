package com.ex.learninghub.modules.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String recipientEmail;
    private String subject;
    private String content;
    private String notificationType; // EMAIL, SYSTEM, WEBSOCKET
}
