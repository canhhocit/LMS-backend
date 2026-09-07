package com.ex.learninghub.modules.course.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clazz_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClazzPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String description;
}
