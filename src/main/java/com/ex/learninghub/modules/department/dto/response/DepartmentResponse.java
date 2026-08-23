package com.ex.learninghub.modules.department.dto.response;

import com.ex.learninghub.modules.department.entity.Department;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long headUserId;
    private String headUserFullName;
    private Boolean isActive;

    public static DepartmentResponse from(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .code(d.getCode())
                .name(d.getName())
                .description(d.getDescription())
                .headUserId(d.getHeadUserId())
                .isActive(d.getIsActive())
                .build();
    }
}
