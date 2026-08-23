package com.ex.learninghub.modules.curriculum.dto.response;

import com.ex.learninghub.modules.curriculum.entity.CurriculumCourse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumCourseResponse {
    private Long id;
    private Long curriculumId;
    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private Integer credits;
    private Integer semesterNo;
    private Boolean isRequired;

    public static CurriculumCourseResponse from(CurriculumCourse cc) {
        CurriculumCourseResponseBuilder b = builder()
                .id(cc.getId())
                .curriculumId(cc.getCurriculumId())
                .courseId(cc.getCourseId())
                .semesterNo(cc.getSemesterNo())
                .isRequired(cc.getIsRequired());
        if (cc.getCourse() != null) {
            b.courseCode(cc.getCourse().getCode())
             .courseTitle(cc.getCourse().getTitle())
             .credits(cc.getCourse().getCredit());
        }
        return b.build();
    }
}
