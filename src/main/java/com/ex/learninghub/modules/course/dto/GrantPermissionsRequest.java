package com.ex.learninghub.modules.course.dto;

import lombok.Data;
import java.util.Set;

@Data
public class GrantPermissionsRequest {
    private Long userId;
    private Set<String> permissionCodes;
}