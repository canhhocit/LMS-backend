package com.ex.learninghub.modules.forum.repository;

import com.ex.learninghub.modules.forum.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByClazzIdOrderByCreatedAtDesc(Long clazzId);
}
