package com.ex.learninghub.modules.user.repository;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByStudentCode(String studentCode);
    Optional<User> findByLecturerCode(String lecturerCode);
    boolean existsByEmail(String email);
    boolean existsByStudentCode(String studentCode);
    boolean existsByLecturerCode(String lecturerCode);
    boolean existsByRole(Role role);
    long countByRole(Role role);
    List<User> findByAdminClassId(Long adminClassId);


    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.lecturerCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> findByRoleAndKeyword(@Param("role") Role role,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);
}