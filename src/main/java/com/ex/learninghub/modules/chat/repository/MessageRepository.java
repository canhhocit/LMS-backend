package com.ex.learninghub.modules.chat.repository;

import com.ex.learninghub.modules.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(Long userId1, Long userId2, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.courseId = :courseId AND " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findByCourseIdAndUsers(Long courseId, Long userId1, Long userId2);

    @Query("SELECT m FROM Message m WHERE m.receiverId = :userId AND m.isRead = false ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessages(Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.senderId = :senderId")
    void markAsRead(Long receiverId, Long senderId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);
}