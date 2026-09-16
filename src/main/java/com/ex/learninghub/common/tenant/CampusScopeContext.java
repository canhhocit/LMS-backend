package com.ex.learninghub.common.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CampusScopeContext {

    private static final ThreadLocal<String> CURRENT_CAMPUS = new ThreadLocal<>();

    public static void setCampus(String campusCode) {
        CURRENT_CAMPUS.set(campusCode);
        log.debug("Đã thiết lập phạm vi cơ sở đào tạo (Campus): {}", campusCode);
    }

    public static String getCampus() {
        return CURRENT_CAMPUS.get() != null ? CURRENT_CAMPUS.get() : "MAIN_CAMPUS";
    }

    public static void clear() {
        CURRENT_CAMPUS.remove();
    }
}
