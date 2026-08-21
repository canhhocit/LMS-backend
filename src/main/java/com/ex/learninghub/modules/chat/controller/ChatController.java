package com.ex.learninghub.modules.chat.controller;

import com.ex.learninghub.modules.chat.dto.MessageDTO;
import com.ex.learninghub.modules.chat.dto.SendMessageDTO;
import com.ex.learninghub.modules.chat.service.ChatService;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR', 'ADMIN')")
    public Page<MessageDTO> getConversation(
            @RequestParam Long withUserId,
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable) {
        return chatService.getConversation(principal.getUser().getId(), withUserId, pageable);
    }

    @GetMapping("/course")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR', 'ADMIN')")
    public List<MessageDTO> getMessagesByCourse(
            @RequestParam Long withUserId,
            @RequestParam Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return chatService.getMessagesByCourse(principal.getUser().getId(), withUserId, courseId);
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR', 'ADMIN')")
    public void markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        chatService.markAsRead(id, principal.getUser().getId());
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR', 'ADMIN')")
    public long getUnreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return chatService.getUnreadCount(principal.getUser().getId());
    }

    // WebSocket handler for sending messages
    @MessageMapping("/chat.send")
    public void handleChatMessage(@Payload SendMessageDTO messageDTO) {
        // This is handled by the WebSocket message broker
        // Messages are persisted and broadcast via ChatService
    }
}