package com.ex.learninghub.modules.audit.repository;

import com.ex.learninghub.modules.audit.entity.SystemErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemErrorLogRepository extends JpaRepository<SystemErrorLog, Long> {

    Page<SystemErrorLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
