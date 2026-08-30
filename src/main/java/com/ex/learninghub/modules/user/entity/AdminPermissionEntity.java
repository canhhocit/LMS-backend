package com.ex.learninghub.modules.user.entity;

import com.ex.learninghub.common.enums.AdminPermission;
import com.ex.learninghub.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_permissions")
public class AdminPermissionEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private AdminPermission code;

    @Column(nullable = false, length = 255)
    private String description;
}
