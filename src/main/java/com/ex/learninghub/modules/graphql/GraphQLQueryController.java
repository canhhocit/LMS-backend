package com.ex.learninghub.modules.graphql;

import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/graphql")
public class GraphQLQueryController {

    private static final Logger log = LoggerFactory.getLogger(GraphQLQueryController.class);

    private final CourseRepository courseRepository;

    public GraphQLQueryController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public static class GraphQLRequest {
        private String query;
        private Map<String, Object> variables;

        public GraphQLRequest() {
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }
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
