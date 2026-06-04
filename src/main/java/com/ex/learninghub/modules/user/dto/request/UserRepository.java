package com.ex.learninghub.modules.user.dto.request;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.learninghub.modules.user.entity.User;


public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
