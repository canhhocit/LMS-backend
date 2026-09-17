package com.ex.learninghub.modules.user.repository;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
    long countByAdminClassId(Long adminClassId);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) OR (u.personalEmail IS NOT NULL AND LOWER(u.personalEmail) = LOWER(:email))")
    Optional<User> findByEmailOrPersonalEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:identifier) OR (u.studentCode IS NOT NULL AND LOWER(u.studentCode) = LOWER(:identifier)) OR (u.lecturerCode IS NOT NULL AND LOWER(u.lecturerCode) = LOWER(:identifier))")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    @EntityGraph(attributePaths = {"adminClass", "curriculum"})
    Page<User> findByRoleAndAdminClass_ClassName(Role role, String adminClassName, Pageable pageable);

    @EntityGraph(attributePaths = {"adminClass", "curriculum"})
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.adminClass.className = :adminClassName AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.lecturerCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> findByRoleAdminClassAndKeyword(@Param("role") Role role,
                                              @Param("adminClassName") String adminClassName,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    @EntityGraph(attributePaths = {"adminClass", "curriculum"})
    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.lecturerCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> findByRoleAndKeyword(@Param("role") Role role,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);

    @EntityGraph(attributePaths = {"adminClass", "curriculum"})
    Page<User> findByRole(Role role, Pageable pageable);

    List<User> findByRole(Role role);
}