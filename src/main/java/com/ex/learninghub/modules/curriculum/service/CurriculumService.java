package com.ex.learninghub.modules.curriculum.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumCourseRequest;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumRequest;
import com.ex.learninghub.modules.curriculum.dto.request.PrerequisiteRequest;
import com.ex.learninghub.modules.curriculum.dto.response.CurriculumCourseResponse;
import com.ex.learninghub.modules.curriculum.dto.response.CurriculumResponse;
import com.ex.learninghub.modules.curriculum.dto.response.PrerequisiteResponse;

import java.util.List;

public interface CurriculumService {

    // Curriculum CRUD
    CurriculumResponse createCurriculum(CurriculumRequest request);
    CurriculumResponse updateCurriculum(Long id, CurriculumRequest request);
    void deleteCurriculum(Long id);
    List<CurriculumResponse> listCurricula();
    CurriculumResponse getCurriculum(Long id);

    // CurriculumCourse management
    CurriculumCourseResponse addCourseToCurriculum(Long curriculumId, CurriculumCourseRequest request);
    void removeCourseFromCurriculum(Long curriculumId, Long courseId);
    List<CurriculumCourseResponse> listCoursesByCurriculum(Long curriculumId);

    // Prerequisite management
    PrerequisiteResponse addPrerequisite(Long courseId, PrerequisiteRequest request);
    void removePrerequisite(Long courseId, Long prerequisiteCourseId);
    List<PrerequisiteResponse> listPrerequisites(Long courseId);

    /**
     * Kiểm tra sinh viên đã hoàn thành tất cả môn tiên quyết của courseId chưa.
     * Trả về danh sách các courseId tiên quyết CHƯA đạt (rỗng = đủ điều kiện).
     */
    List<Long> checkMissingPrerequisites(Long courseId, UserPrincipal principal);
}
