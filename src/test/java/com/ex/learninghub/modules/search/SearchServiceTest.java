package com.ex.learninghub.modules.search;

import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import com.ex.learninghub.modules.search.service.GlobalSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private GlobalSearchService globalSearchService;

    @Test
    @DisplayName("Nên tìm kiếm kết quả theo từ khóa thành công")
    void globalSearch_Success() {
        Course c1 = Course.builder().code("CS101").title("Lập trình Java Căn Bản").credit(3).build();
        c1.setId(1L);
        when(courseRepository.findAll()).thenReturn(List.of(c1));

        List<GlobalSearchService.SearchResultItem> items = globalSearchService.globalSearch("Java");

        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getTitle()).contains("Java");
    }
}
