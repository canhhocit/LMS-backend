package com.ex.learninghub.modules.enrollment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for enrolling multiple students into a class (Clazz).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollStudentsRequest {

    /** List of student (User) IDs to enroll */
    private List<Long> studentIds;
}