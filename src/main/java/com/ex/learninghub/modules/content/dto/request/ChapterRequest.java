package com.ex.learninghub.modules.content.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChapterRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private Integer sortOrder = 0;
}
