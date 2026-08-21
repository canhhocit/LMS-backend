package com.ex.learninghub.modules.chat.mapper;

import com.ex.learninghub.modules.chat.dto.MessageDTO;
import com.ex.learninghub.modules.chat.entity.Message;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }
        
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .courseId(message.getCourseId())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public MessageDTO toDTO(Message message, User sender, User receiver, Course course) {
        if (message == null) {
            return null;
        }
        
        MessageDTO dto = toDTO(message);
        if (sender != null) {
            dto.setSenderName(sender.getFullName());
        }
        if (receiver != null) {
            dto.setReceiverName(receiver.getFullName());
        }
        if (course != null) {
            dto.setCourseName(course.getCourseName());
        }
        return dto;
    }

    public Message toEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Message.builder()
                .id(dto.getId())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .courseId(dto.getCourseId())
                .content(dto.getContent())
                .isRead(dto.getIsRead())
                .build();
    }
}