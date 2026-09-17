package com.ex.learninghub.modules.registration.repository;

import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    Optional<RegistrationPeriod> findByIsActiveTrue();

    @Modifying
    @Query("UPDATE RegistrationPeriod r SET r.isActive = false WHERE r.isActive = true")
    void deactivateAllActive();
}

