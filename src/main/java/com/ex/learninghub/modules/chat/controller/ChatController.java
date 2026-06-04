package com.ex.learninghub.modules.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.modules.chat.dto.request.MessageSendRequest;
import com.ex.learninghub.modules.chat.dto.response.MessageResponse;
import com.ex.learninghub.modules.chat.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Module", description = "Endpoints for messaging and WebSocket chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    @Operation(summary = "Send a direct message via HTTP REST")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @Valid @RequestBody MessageSendRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MessageResponse response = chatService.sendMessage(request, userDetails.getUsername());
        
        messagingTemplate.convertAndSendToUser(
                response.getReceiverId().toString(), 
                "/queue/messages", 
                response
        );

        return ResponseEntity.ok(
                ApiResponse.<MessageResponse>builder()
                        .code(200)
                        .message("Message sent successfully")
                        .result(response)
                        .build()
        );
    }

    @MessageMapping("/chat.send")
    public void processMessage(@Payload MessageSendRequest request, Principal principal) {
        MessageResponse response = chatService.sendMessage(request, principal.getName());
        messagingTemplate.convertAndSendToUser(
                response.getReceiverId().toString(), 
                "/queue/messages", 
                response
        );
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get message history with another user")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getChatHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> response = chatService.getChatHistory(userId, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.<List<MessageResponse>>builder()
                        .code(200)
                        .message("Chat history retrieved successfully")
                        .result(response)
                        .build()
        );
    }
}
