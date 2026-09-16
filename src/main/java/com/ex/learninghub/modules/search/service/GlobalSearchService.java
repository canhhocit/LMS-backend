package com.ex.learninghub.modules.search.service;

import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final CourseRepository courseRepository;

    @Data
    @Builder
    public static class SearchResultItem {
        private String type; // COURSE, LESSON, FORUM
        private String title;
        private String description;
        private String link;
    }

    public List<SearchResultItem> globalSearch(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }

        String keyword = query.trim().toLowerCase();
        List<SearchResultItem> results = new ArrayList<>();

        // Search Courses
        List<Course> courses = courseRepository.findAll();
        for (Course c : courses) {
            if ((c.getTitle() != null && c.getTitle().toLowerCase().contains(keyword)) ||
                (c.getCode() != null && c.getCode().toLowerCase().contains(keyword))) {
                results.add(SearchResultItem.builder()
                        .type("COURSE")
                        .title(c.getTitle())
                        .description("Mã học phần: " + c.getCode() + " | Số tín chỉ: " + c.getCredit())
                        .link("/courses/" + c.getId())
                        .build());
            }
        }

        // Mock Fuzzy Search Results for Forum & Lessons
        if ("java".contains(keyword) || "spring".contains(keyword) || "lập trình".contains(keyword)) {
            results.add(SearchResultItem.builder()
                    .type("LESSON")
                    .title("Bài 1: Giới thiệu Lập trình Java & Spring Boot")
                    .description("Nội dung bài học về cấu trúc kiến trúc Spring Boot REST API")
                    .link("/lessons/1")
                    .build());
            results.add(SearchResultItem.builder()
                    .type("FORUM")
                    .title("Hỏi đáp về cách cài đặt Redis Caching trong Spring Boot")
                    .description("Thảo luận diễn đàn sinh viên về cấu hình Spring Data Redis")
                    .link("/forum/posts/5")
                    .build());
        }

        log.info("Tìm kiếm toàn văn cho từ khóa '{}' tìm thấy {} kết quả", query, results.size());
        return results;
    }
}
