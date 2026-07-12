package com.ex.learninghub.modules.content.repository;

import com.ex.learninghub.modules.content.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByClazzIdOrderByCreatedAtDesc(Long clazzId);
}
