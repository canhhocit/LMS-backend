package com.ex.learninghub.modules.chat.service;

import com.ex.learninghub.modules.chat.dto.request.MessageSendRequest;
import com.ex.learninghub.modules.chat.dto.response.MessageResponse;
import java.util.List;

public interface ChatService {
    MessageResponse sendMessage(MessageSendRequest request, String senderEmail);
    List<MessageResponse> getChatHistory(Long userId, String email);
}
