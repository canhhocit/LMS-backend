package com.ex.learninghub.common.enums;

public enum ClazzPermissionCode {
    MANAGE_CONTENT("Quản lý chương, bài học, video của lớp"),
    MANAGE_GRADING("Nhập điểm, điểm danh, chấm bài tập/quiz"),
    MANAGE_ANNOUNCEMENT("Đăng và sửa thông báo của lớp"),
    MANAGE_FORUM("Xoá bài viết/bình luận vi phạm trong diễn đàn của lớp"),
    MANAGE_SCHEDULE("Quản lý lịch học của lớp");

    private final String description;

    ClazzPermissionCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
