package com.ex.learninghub.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binding cho các thuộc tính tùy chỉnh prefix "app" trong application.yml.
 * Ví dụ: app.frontend-url, app.cors.allowed-origins, app.upload.max-video-size, ...
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String frontendUrl;
    private String defaultPassword;

    private Cors cors = new Cors();
    private Upload upload = new Upload();
    private Academic academic = new Academic();
    private Attendance attendance = new Attendance();
    private Registration registration = new Registration();
    private Curriculum curriculum = new Curriculum();
    private Admin admin = new Admin();

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins;
    }

    @Getter
    @Setter
    public static class Upload {
        private String maxVideoSize;
        private String maxAvatarSize;
        private String maxAssignmentFileSize;
    }

    @Getter
    @Setter
    public static class Academic {
        private double passScore;
        private double warningGpa;
        private double probationGpa;
    }

    @Getter
    @Setter
    public static class Attendance {
        private double maxAbsentRatio;
    }

    @Getter
    @Setter
    public static class Registration {
        private int maxCreditsProbation;
    }

    @Getter
    @Setter
    public static class Curriculum {
        private double passScore;
    }

    @Getter
    @Setter
    public static class Admin {
        private String defaultEmail;
        private String defaultPassword;
    }
}
