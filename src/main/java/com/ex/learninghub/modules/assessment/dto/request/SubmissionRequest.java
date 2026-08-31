package com.ex.learninghub.modules.assessment.dto.request;

import com.ex.learninghub.common.enums.SubmissionType;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubmissionRequest {

    private SubmissionType submissionType = SubmissionType.FILE;

    private String fileUrl;

    private List<String> fileUrls = new ArrayList<>();

    @Pattern(regexp = "^https?:\\/\\/.*$", message = "External link must be a valid HTTP(S) URL")
    private String externalLink;
}
