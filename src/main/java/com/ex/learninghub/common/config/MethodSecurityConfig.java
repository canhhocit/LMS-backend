package com.ex.learninghub.common.config;

import com.ex.learninghub.common.security.AppPermissionEvaluator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

@Configuration
public class MethodSecurityConfig {

    private final AppPermissionEvaluator appPermissionEvaluator;

    public MethodSecurityConfig(@Lazy AppPermissionEvaluator appPermissionEvaluator) {
        this.appPermissionEvaluator = appPermissionEvaluator;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(appPermissionEvaluator);
        return handler;
    }
}
