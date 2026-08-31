package com.ex.learninghub.modules.curriculum.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumCourseRequest;
import com.ex.learninghub.modules.curriculum.dto.request.CurriculumRequest;
import com.ex.learninghub.modules.curriculum.dto.request.PrerequisiteRequest;
import com.ex.learninghub.modules.curriculum.dto.response.CurriculumCourseResponse;
import com.ex.learninghub.modules.curriculum.dto.response.CurriculumResponse;
import com.ex.learninghub.modules.curriculum.dto.response.PrerequisiteResponse;
import com.ex.learninghub.modules.curriculum.entity.CoursePrerequisite;
import com.ex.learninghub.modules.curriculum.entity.Curriculum;
import com.ex.learninghub.modules.curriculum.entity.CurriculumCourse;
import com.ex.learninghub.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.ex.learninghub.modules.curriculum.repository.CurriculumCourseRepository;
import com.ex.learninghub.modules.curriculum.repository.CurriculumRepository;
import com.ex.learninghub.modules.curriculum.service.CurriculumService;
import com.ex.learninghub.modules.grading.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final CurriculumCourseRepository curriculumCourseRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    @Value("${app.curriculum.pass-score:5.0}")
    private BigDecimal passScore;

    // =================== Curriculum CRUD ===================

    @Override
    @Transactional
    public CurriculumResponse createCurriculum(CurriculumRequest request) {
        Curriculum c = Curriculum.builder()
                .name(request.getName())
                .faculty(request.getFaculty())
                .academicYear(request.getAcademicYear())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .build();
        return CurriculumResponse.from(curriculumRepository.save(c));
    }

    @Override
    @Transactional
    public CurriculumResponse updateCurriculum(Long id, CurriculumRequest request) {
        Curriculum c = curriculumRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CURRICULUM_NOT_FOUND));
        c.setName(request.getName());
        c.setFaculty(request.getFaculty());
        c.setAcademicYear(request.getAcademicYear());
        if (request.getIsActive() != null) c.setIsActive(request.getIsActive());
        return CurriculumResponse.from(curriculumRepository.save(c));
    }

    @Override
    @Transactional
    public void deleteCurriculum(Long id) {
        curriculumRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> listCurricula() {
        return curriculumRepository.findAll().stream()
                .map(CurriculumResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumResponse getCurriculum(Long id) {
        return curriculumRepository.findById(id)
                .map(CurriculumResponse::from)
                .orElseThrow(() -> new AppException(ErrorCode.CURRICULUM_NOT_FOUND));
    }

    // =================== CurriculumCourse ===================

    @Override
    @Transactional
    public CurriculumCourseResponse addCourseToCurriculum(Long curriculumId, CurriculumCourseRequest request) {
        if (!curriculumRepository.existsById(curriculumId)) {
            throw new AppException(ErrorCode.CURRICULUM_NOT_FOUND);
        }
        courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        CurriculumCourse cc = curriculumCourseRepository
                .findByCurriculumIdAndCourseId(curriculumId, request.getCourseId())
                .orElseGet(() -> CurriculumCourse.builder()
                        .curriculumId(curriculumId)
                        .courseId(request.getCourseId())
                        .build());
        cc.setSemesterNo(request.getSemesterNo());
        cc.setIsRequired(request.getIsRequired() == null ? Boolean.TRUE : request.getIsRequired());
        return CurriculumCourseResponse.from(curriculumCourseRepository.save(cc));
    }

    @Override
    @Transactional
    public void removeCourseFromCurriculum(Long curriculumId, Long courseId) {
        curriculumCourseRepository.deleteByCurriculumIdAndCourseId(curriculumId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumCourseResponse> listCoursesByCurriculum(Long curriculumId) {
        return curriculumCourseRepository.findByCurriculumId(curriculumId).stream()
                .map(CurriculumCourseResponse::from)
                .sorted((a, b) -> Integer.compare(a.getSemesterNo(), b.getSemesterNo()))
                .collect(Collectors.toList());
    }

    // =================== Prerequisite ===================

    @Override
    @Transactional
    public PrerequisiteResponse addPrerequisite(Long courseId, PrerequisiteRequest request) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Course prereq = courseRepository.findById(request.getPrerequisiteCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (courseId.equals(request.getPrerequisiteCourseId())) {
            throw new AppException(ErrorCode.PREREQUISITE_NOT_MET);
        }

        if (prerequisiteRepository.existsByCourseIdAndPrerequisiteCourseId(
                courseId, request.getPrerequisiteCourseId())) {
            throw new AppException(ErrorCode.PREREQUISITE_NOT_MET);
        }

        if (wouldCreateCycle(courseId, request.getPrerequisiteCourseId())) {
            throw new AppException(ErrorCode.PREREQUISITE_NOT_MET);
        }

        CoursePrerequisite cp = CoursePrerequisite.builder()
                .courseId(courseId)
                .prerequisiteCourseId(request.getPrerequisiteCourseId())
                .build();
        CoursePrerequisite saved = prerequisiteRepository.save(cp);

        return PrerequisiteResponse.builder()
                .id(saved.getId())
                .courseId(courseId)
                .prerequisiteCourseId(prereq.getId())
                .prerequisiteCourseCode(prereq.getCode())
                .prerequisiteCourseTitle(prereq.getTitle())
                .build();
    }

    @Override
    @Transactional
    public void removePrerequisite(Long courseId, Long prerequisiteCourseId) {
        prerequisiteRepository.deleteByCourseIdAndPrerequisiteCourseId(courseId, prerequisiteCourseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrerequisiteResponse> listPrerequisites(Long courseId) {
        return prerequisiteRepository.findByCourseId(courseId).stream()
                .map(cp -> {
                    PrerequisiteResponse.PrerequisiteResponseBuilder b = PrerequisiteResponse.builder()
                            .id(cp.getId())
                            .courseId(cp.getCourseId())
                            .prerequisiteCourseId(cp.getPrerequisiteCourseId());
                    courseRepository.findById(cp.getPrerequisiteCourseId())
                            .ifPresent(p -> b.prerequisiteCourseCode(p.getCode())
                                    .prerequisiteCourseTitle(p.getTitle()));
                    return b.build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Trả về danh sách courseId tiên quyết CHƯA đạt.
     * Nếu rỗng → sinh viên đủ điều kiện đăng ký.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> checkMissingPrerequisites(Long courseId, UserPrincipal principal) {
        List<CoursePrerequisite> prereqs = prerequisiteRepository.findByCourseId(courseId);
        if (prereqs.isEmpty()) return List.of();

        List<Long> passed = gradeRepository.findPassedCourseIds(
                principal.getUser().getId(), passScore);

        return prereqs.stream()
                .map(CoursePrerequisite::getPrerequisiteCourseId)
                .filter(id -> !passed.contains(id))
                .collect(Collectors.toList());
    }

    private boolean wouldCreateCycle(Long courseId, Long prerequisiteCourseId) {
        java.util.Set<Long> visited = new java.util.HashSet<>();
        return hasPath(prerequisiteCourseId, courseId, visited);
    }

    private boolean hasPath(Long currentCourseId, Long targetCourseId, java.util.Set<Long> visited) {
        if (currentCourseId == null) {
            return false;
        }
        if (currentCourseId.equals(targetCourseId)) {
            return true;
        }
        if (!visited.add(currentCourseId)) {
            return false;
        }

        List<CoursePrerequisite> incoming = prerequisiteRepository.findByCourseId(currentCourseId);
        for (CoursePrerequisite prerequisite : incoming) {
            if (hasPath(prerequisite.getPrerequisiteCourseId(), targetCourseId, visited)) {
                return true;
            }
        }
        return false;
    }
}
