package com.ex.learninghub.modules.registration.repository;

import com.ex.learninghub.modules.registration.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    Optional<RegistrationPeriod> findByIsActiveTrue();
}
