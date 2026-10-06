package com.ex.learninghub.modules.registration.repository;

import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    Optional<RegistrationPeriod> findByIsActiveTrue();

    @Modifying
    @Query("UPDATE RegistrationPeriod r SET r.isActive = false WHERE r.isActive = true")
    void deactivateAllActive();

    @Modifying
    @Query("UPDATE RegistrationPeriod r SET r.isActive = false WHERE r.isActive = true AND r.closeAt <= :now")
    int deactivateExpiredActive(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM RegistrationPeriod p LEFT JOIN FETCH p.allowedClasses WHERE p.isActive = true")
    Optional<RegistrationPeriod> findActiveWithClasses();
}
