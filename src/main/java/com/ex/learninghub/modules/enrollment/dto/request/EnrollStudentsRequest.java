package com.ex.learninghub.modules.enrollment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EnrollStudentsRequest {

    @NotEmpty(message = "Student IDs must not be empty")
    private List<Long> studentIds;
}
