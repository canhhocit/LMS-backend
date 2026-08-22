package com.ex.learninghub.modules.admin.service;

import java.util.Map;

public interface AdminService {
    Map<String, Object> getDashboardStats();

    Map<String, Object> getEnrollmentsByMonth();

    Map<String, Double> getAverageScoreByClazz();
}