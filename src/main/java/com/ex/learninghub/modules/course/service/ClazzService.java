package com.ex.learninghub.modules.course.service;

import com.ex.learninghub.modules.course.dto.request.ClazzRequest;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClazzService {

    ClazzResponse createClazz(ClazzRequest request);

    ClazzResponse updateClazz(Long id, ClazzRequest request);

    void deleteClazz(Long id);

    List<ClazzResponse> getAllClazzes();

    Page<ClazzResponse> getAllClazzes(Pageable pageable);

    ClazzResponse getClazzById(Long id);

    List<ClazzResponse> getClazzesByLecturer(Long lecturerId);
}
