package com.ex.learninghub.modules.user.repository;

import com.ex.learninghub.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByStudentCode(String studentCode);
    Optional<User> findByLecturerCode(String lecturerCode);
    boolean existsByEmail(String email);
    boolean existsByStudentCode(String studentCode);
    boolean existsByLecturerCode(String lecturerCode);
}
