package com.ex.learninghub.modules.graphql;

import com.ex.learninghub.modules.course.entity.Course;
import com.ex.learninghub.modules.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphQLQueryControllerTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private GraphQLQueryController graphQLQueryController;

    @Test
    @DisplayName("Nên thực thi GraphQL query trả về danh sách khóa học thành công")
    void executeGraphQLQuery_Success() {
        Course c = Course.builder().code("CS101").title("Java Core").credit(3).build();
        c.setId(10L);
        when(courseRepository.findAll()).thenReturn(List.of(c));

        GraphQLQueryController.GraphQLRequest req = GraphQLQueryController.GraphQLRequest.builder()
                .query("{ courses { id code title credit } }")
                .build();

        ResponseEntity<Map<String, Object>> response = graphQLQueryController.executeGraphQLQuery(req);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("data");
    }
}
