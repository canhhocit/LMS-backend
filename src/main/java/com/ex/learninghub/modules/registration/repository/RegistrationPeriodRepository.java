package com.ex.learninghub.modules.registration.repository;

import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    Optional<RegistrationPeriod> findByIsActiveTrue();
}
