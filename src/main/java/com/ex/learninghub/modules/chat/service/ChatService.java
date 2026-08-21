package com.ex.learninghub.modules.chat.service;

import com.ex.learninghub.modules.chat.dto.MessageDTO;
import com.ex.learninghub.modules.chat.dto.SendMessageDTO;
import com.ex.learninghub.modules.chat.entity.Message;
import com.ex.learninghub.modules.chat.mapper.MessageMapper;
import com.ex.learninghub.modules.chat.repository.MessageRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDTO sendMessage(Long senderId, SendMessageDTO dto) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check if sender is enrolled in the course (for learner-mentor chat)
        if (dto.getCourseId() != null) {
            boolean isEnrolled = enrollmentRepository.existsByLearnerIdAndCourseId(senderId, dto.getCourseId());
            boolean isMentorOfCourse = enrollmentRepository.existsByCourseIdAndMentorId(dto.getCourseId(), senderId);
            
            if (!isEnrolled && !isMentorOfCourse) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }

        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(dto.getReceiverId())
                .courseId(dto.getCourseId())
                .content(dto.getContent())
                .isRead(false)
                .build();

        message = messageRepository.save(message);
        MessageDTO messageDTO = messageMapper.toDTO(message);

        // Send real-time message via WebSocket
        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/messages",
                messageDTO
        );

        return messageDTO;
    }

    public Page<MessageDTO> getConversation(Long userId, Long otherUserId, Pageable pageable) {
        Page<Message> messages = messageRepository.findConversation(userId, otherUserId, pageable);
        return messages.map(messageMapper::toDTO);
    }

    public List<MessageDTO> getMessagesByCourse(Long userId, Long otherUserId, Long courseId) {
        List<Message> messages = messageRepository.findByCourseIdAndUsers(
                courseId, userId, otherUserId);
        return messages.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (message.getReceiverId().equals(userId)) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    public long getUnreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}
