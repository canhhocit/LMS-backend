package com.ex.learninghub.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminClassRequest {

    @NotBlank(message = "Class name cannot be blank")
    private String className;

    private String faculty;

    private String academicYear;
}
