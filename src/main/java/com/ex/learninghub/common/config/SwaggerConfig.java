package com.ex.learninghub.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LearningHub LMS API")
                        .version("1.0.0")
                        .description("Tài liệu API cho hệ thống quản lý học tập LearningHub (LMS) - "
                                + "nền tảng hỗ trợ đào tạo theo mô hình Clazz-based cho trường đại học. "
                                + "Hệ thống cung cấp các API phục vụ cho Admin, Giảng viên và Sinh viên.")
                        .contact(new Contact()
                                .name("LearningHub Team")
                                .email("support@learninghub.example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("All APIs")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authServiceApi() {
        return groupByModule("Auth Service", "com.ex.learninghub.modules.auth");
    }

    @Bean
    public GroupedOpenApi userServiceApi() {
        return groupByModule("User Service", "com.ex.learninghub.modules.user");
    }

    @Bean
    public GroupedOpenApi courseServiceApi() {
        return groupByModule("Course Service", "com.ex.learninghub.modules.course");
    }

    @Bean
    public GroupedOpenApi contentServiceApi() {
        return groupByModule("Content Service", "com.ex.learninghub.modules.content");
    }

    @Bean
    public GroupedOpenApi assessmentServiceApi() {
        return groupByModule("Assessment Service", "com.ex.learninghub.modules.assessment");
    }

    @Bean
    public GroupedOpenApi quizServiceApi() {
        return groupByModule("Quiz Service", "com.ex.learninghub.modules.quiz");
    }

    @Bean
    public GroupedOpenApi gradingServiceApi() {
        return groupByModule("Grading Service", "com.ex.learninghub.modules.grading");
    }

    @Bean
    public GroupedOpenApi scheduleServiceApi() {
        return groupByModule("Schedule Service", "com.ex.learninghub.modules.schedule");
    }

    @Bean
    public GroupedOpenApi registrationServiceApi() {
        return groupByModule("Registration Service", "com.ex.learninghub.modules.registration");
    }

    @Bean
    public GroupedOpenApi curriculumServiceApi() {
        return groupByModule("Curriculum Service", "com.ex.learninghub.modules.curriculum");
    }

    @Bean
    public GroupedOpenApi departmentServiceApi() {
        return groupByModule("Department Service", "com.ex.learninghub.modules.department");
    }

    @Bean
    public GroupedOpenApi tuitionServiceApi() {
        return groupByModule("Tuition Service", "com.ex.learninghub.modules.tuition");
    }

    @Bean
    public GroupedOpenApi notificationServiceApi() {
        return groupByModule("Notification Service", "com.ex.learninghub.modules.notification");
    }

    @Bean
    public GroupedOpenApi forumServiceApi() {
        return groupByModule("Forum Service", "com.ex.learninghub.modules.forum");
    }

    @Bean
    public GroupedOpenApi adminServiceApi() {
        return groupByModule("Admin Service", "com.ex.learninghub.modules.admin");
    }

    private GroupedOpenApi groupByModule(String groupName, String packageName) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .packagesToScan(packageName)
                .build();
    }
}
