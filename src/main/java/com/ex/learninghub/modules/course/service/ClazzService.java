package com.ex.learninghub.modules.course.service;

import com.ex.learninghub.modules.course.dto.request.ClazzRequest;
import com.ex.learninghub.modules.course.dto.response.ClazzResponse;

import java.util.List;

public interface ClazzService {

    ClazzResponse createClazz(ClazzRequest request);

    ClazzResponse updateClazz(Long id, ClazzRequest request);

    void deleteClazz(Long id);

    List<ClazzResponse> getAllClazzes();

    ClazzResponse getClazzById(Long id);

    List<ClazzResponse> getClazzesByLecturer(Long lecturerId);
}
