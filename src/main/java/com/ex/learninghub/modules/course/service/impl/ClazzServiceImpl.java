package com.ex.learninghub.modules.course.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.course.dto.request.ClazzRequest;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.course.service.ClazzService;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClazzServiceImpl implements ClazzService {

    private final ClazzRepository clazzRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ClazzResponse createClazz(ClazzRequest request) {
        if (clazzRepository.existsByClassCode(request.getClassCode())) {
            throw new AppException(ErrorCode.CLAZZ_ALREADY_EXISTS);
        }
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        User lecturer = null;
        if (request.getLecturerId() != null) {
            lecturer = userRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
        Clazz clazz = Clazz.builder()
                .classCode(request.getClassCode())
                .className(request.getClassName())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .course(course)
                .lecturer(lecturer)
                .build();
        return ClazzResponse.from(clazzRepository.save(clazz));
    }

    @Override
    @Transactional
    public ClazzResponse updateClazz(Long id, ClazzRequest request) {
        Clazz clazz = clazzRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        User lecturer = null;
        if (request.getLecturerId() != null) {
            lecturer = userRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
        clazz.setClassCode(request.getClassCode());
        clazz.setClassName(request.getClassName());
        clazz.setSemester(request.getSemester());
        clazz.setAcademicYear(request.getAcademicYear());
        clazz.setCourse(course);
        clazz.setLecturer(lecturer);
        return ClazzResponse.from(clazzRepository.save(clazz));
    }

    @Override
    @Transactional
    public void deleteClazz(Long id) {
        Clazz clazz = clazzRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        clazzRepository.delete(clazz);
    }

    @Override
    public List<ClazzResponse> getAllClazzes() {
        return clazzRepository.findAll().stream()
                .map(ClazzResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ClazzResponse> getAllClazzes(Pageable pageable) {
        return clazzRepository.findAll(pageable).map(ClazzResponse::from);
    }

    @Override
    public ClazzResponse getClazzById(Long id) {
        Clazz clazz = clazzRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        return ClazzResponse.from(clazz);
    }

    @Override
    public List<ClazzResponse> getClazzesByLecturer(Long lecturerId) {
        return clazzRepository.findByLecturerId(lecturerId).stream()
                .map(ClazzResponse::from)
                .collect(Collectors.toList());
    }
}
