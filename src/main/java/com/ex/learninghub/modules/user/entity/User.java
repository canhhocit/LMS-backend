package com.ex.learninghub.modules.user.entity;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "student_code", unique = true, length = 50)
    private String studentCode;

    @Column(name = "lecturer_code", unique = true, length = 50)
    private String lecturerCode;

    @Column(length = 100)
    private String faculty;

    @Column(length = 100)
    private String major;

    @Column(name = "is_first_login", columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean isFirstLogin = true;
}
