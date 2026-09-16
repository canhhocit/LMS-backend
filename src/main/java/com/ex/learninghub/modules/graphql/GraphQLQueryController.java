package com.ex.learninghub.modules.graphql;

import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/graphql")
@RequiredArgsConstructor
public class GraphQLQueryController {

    private final CourseRepository courseRepository;

    @Data
    @Builder
    public static class GraphQLRequest {
        private String query;
        private Map<String, Object> variables;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> executeGraphQLQuery(@RequestBody GraphQLRequest request) {
        log.info("Xử lý GraphQL Query Request: {}", request.getQuery());
        List<Course> courses = courseRepository.findAll();

        List<Map<String, Object>> courseData = new ArrayList<>();
        for (Course c : courses) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("code", c.getCode());
            map.put("title", c.getTitle());
            map.put("credit", c.getCredit());
            courseData.add(map);
        }

        Map<String, Object> data = Map.of("courses", courseData);
        return ResponseEntity.ok(Map.of("data", data));
    }
}
