package com.ex.learninghub.common.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CampusScopeTest {

    @AfterEach
    void tearDown() {
        CampusScopeContext.clear();
    }

    @Test
    @DisplayName("Nên quản lý ThreadLocal mã cơ sở đào tạo chính xác")
    void campusScope_ThreadLocal() {
        CampusScopeContext.setCampus("CAMPUS_HO_CHI_MINH");

        assertThat(CampusScopeContext.getCampus()).isEqualTo("CAMPUS_HO_CHI_MINH");

        CampusScopeContext.clear();
        assertThat(CampusScopeContext.getCampus()).isEqualTo("MAIN_CAMPUS");
    }
}
