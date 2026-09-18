package com.ex.learninghub.modules.auth.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.auth.dto.MenuItemResponse;
import com.ex.learninghub.modules.auth.repository.PermissionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DynamicMenuService {

    private final PermissionRequestRepository requestRepository;

    public List<MenuItemResponse> getUserMenu(UserPrincipal principal) {
        String role = principal.getUser().getRole().name();
        List<MenuItemResponse> menu = new ArrayList<>();

        if ("STUDENT".equalsIgnoreCase(role)) {
            menu.add(new MenuItemResponse("nav_dashboard", "Trang chủ", "/student", "Home", null));
            menu.add(new MenuItemResponse("nav_classes", "Lớp học của tôi", "/student/classes", "BookOpen", null));
            menu.add(new MenuItemResponse("nav_schedule", "Lịch học", "/student/schedule", "Calendar", null));
            menu.add(new MenuItemResponse("nav_grades", "Kết quả học tập", "/student/grades", "BarChart", null));
            menu.add(new MenuItemResponse("nav_reg", "Đăng ký học phần", "/student/registration", "ClipboardList", null));
            menu.add(new MenuItemResponse("nav_tuition", "Học phí & Đơn từ", "/student/tuition", "CreditCard", null));
            menu.add(new MenuItemResponse("nav_ai", "Trợ lý AI Mascot", "/student/ai-advisor", "Brain", null));
        } else if ("LECTURER".equalsIgnoreCase(role)) {
            menu.add(new MenuItemResponse("nav_dashboard", "Trang chủ", "/lecturer", "Home", null));
            menu.add(new MenuItemResponse("nav_classes", "Lớp giảng dạy", "/lecturer/classes", "BookOpen", null));
            menu.add(new MenuItemResponse("nav_grading", "Nhập điểm Học phần", "/lecturer/grading", "FileCheck", null));
            menu.add(new MenuItemResponse("nav_schedule", "Lịch dạy học", "/lecturer/schedule", "Calendar", null));
            menu.add(new MenuItemResponse("nav_analytics", "Báo cáo Analytics", "/lecturer/analytics", "TrendingUp", null));
            menu.add(new MenuItemResponse("nav_homeroom", "Điểm rèn luyện (GVCN)", "/lecturer/homeroom", "Users", null));
            menu.add(new MenuItemResponse("nav_pbac_req", "Yêu cầu Cấp quyền PBAC", "/lecturer/permission-requests", "Key", null));

            // Dynamic check: If lecturer has an active APPROVED PBAC grant
            boolean hasActiveGrant = requestRepository.findByLecturerIdOrderByCreatedAtDesc(principal.getUser().getId())
                    .stream()
                    .anyMatch(r -> "APPROVED".equals(r.getStatus()) && (r.getValidUntil() == null || r.getValidUntil().isAfter(LocalDateTime.now())));

            if (hasActiveGrant) {
                menu.add(new MenuItemResponse("nav_pbac_active", "⚡ Sửa điểm Lớp học (Quyền tạm thời)", "/lecturer/grading?unlocked=true", "Unlock", null));
            }
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            menu.add(new MenuItemResponse("nav_dashboard", "Tổng quan Admin", "/admin", "Home", null));
            menu.add(new MenuItemResponse("nav_users", "Quản lý Người dùng", "/admin/users", "Users", null));
            menu.add(new MenuItemResponse("nav_classes", "Quản lý Lớp học", "/admin/classes", "BookOpen", null));
            menu.add(new MenuItemResponse("nav_pbac_admin", "Phê duyệt PBAC & Audit Logs", "/admin/pbac-approvals", "ShieldCheck", null));
            menu.add(new MenuItemResponse("nav_reports", "Báo cáo & Xuất Excel/PDF", "/admin/reports", "FileText", null));
            menu.add(new MenuItemResponse("nav_docs", "Kho Biểu mẫu & Biểu mẫu", "/admin/documents", "Folder", null));
        }

        return menu;
    }
}
