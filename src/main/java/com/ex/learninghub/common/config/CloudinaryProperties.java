package com.ex.learninghub.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binding cho các thuộc tính tùy chỉnh prefix "cloudinary" trong application.yml.
 * Ví dụ: cloudinary.cloud-name, cloudinary.api-key, cloudinary.api-secret
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {

    private String cloudName;
    private String apiKey;
    private String apiSecret;
}
