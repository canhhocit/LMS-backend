package com.ex.learninghub.modules.content.controller;

import com.ex.learninghub.common.response.ApiResponse;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.request.AnnouncementRequest;
import com.ex.learninghub.modules.content.dto.request.ChapterRequest;
import com.ex.learninghub.modules.content.dto.request.LessonRequest;
import com.ex.learninghub.modules.content.dto.response.AnnouncementResponse;
import com.ex.learninghub.modules.content.dto.response.ChapterResponse;
import com.ex.learninghub.modules.content.dto.response.LessonResponse;
import com.ex.learninghub.modules.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Nội dung lớp học", description = "Các API quản lý chương, bài học, thông báo và video cho lớp học phần")
public class ClassContentController {

    private final ContentService contentService;

    // ─── Chapters ────────────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/chapters")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Tạo chương mới cho lớp học phần",
            description = "Giảng viên tạo một chương (Chapter) mới thuộc lớp học phần, dùng để nhóm các bài học."
    )
    public ApiResponse<ChapterResponse> createChapter(
            @PathVariable Long classId,
            @Valid @RequestBody ChapterRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.createChapter(classId, request, userPrincipal));
    }

    @PutMapping("/chapters/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Cập nhật chương",
            description = "Giảng viên cập nhật thông tin (tên, thứ tự, mô tả) của một chương."
    )
    public ApiResponse<ChapterResponse> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody ChapterRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.updateChapter(id, request, userPrincipal));
    }

    @DeleteMapping("/chapters/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Xóa chương",
            description = "Giảng viên xóa một chương. Các bài học thuộc chương cũng sẽ bị xóa theo."
    )
    public ApiResponse<Void> deleteChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.deleteChapter(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes/{classId}/chapters")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lấy danh sách chương của lớp học phần",
            description = "Sinh viên/giảng viên thuộc lớp xem danh sách các chương trong lớp học phần."
    )
    public ApiResponse<List<ChapterResponse>> getChaptersByClass(
            @PathVariable Long classId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.verifyAccessToClass(classId, userPrincipal);
        return ApiResponse.success(contentService.getChaptersByClass(classId));
    }

    // ─── Lessons ─────────────────────────────────────────────────────────────────

    @PostMapping("/chapters/{chapterId}/lessons")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Tạo bài học mới trong chương",
            description = "Giảng viên tạo một bài học (Lesson) mới thuộc một chương đã có."
    )
    public ApiResponse<LessonResponse> createLesson(
            @PathVariable Long chapterId,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.createLesson(chapterId, request, userPrincipal));
    }

    @PutMapping("/lessons/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Cập nhật bài học",
            description = "Giảng viên cập nhật nội dung, video, tài liệu của một bài học."
    )
    public ApiResponse<LessonResponse> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.updateLesson(id, request, userPrincipal));
    }

    @DeleteMapping("/lessons/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Xóa bài học",
            description = "Giảng viên xóa một bài học khỏi chương."
    )
    public ApiResponse<Void> deleteLesson(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.deleteLesson(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/chapters/{chapterId}/lessons")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lấy danh sách bài học của một chương",
            description = "Sinh viên/giảng viên thuộc lớp xem danh sách các bài học trong chương."
    )
    public ApiResponse<List<LessonResponse>> getLessonsByChapter(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.verifyAccessToChapter(chapterId, userPrincipal);
        return ApiResponse.success(contentService.getLessonsByChapter(chapterId));
    }

    // ─── Announcements ───────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/announcements")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Tạo thông báo cho lớp học phần",
            description = "Giảng viên đăng một thông báo mới cho lớp học phần của mình."
    )
    public ApiResponse<AnnouncementResponse> createAnnouncement(
            @PathVariable Long classId,
            @Valid @RequestBody AnnouncementRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.createAnnouncement(classId, request, userPrincipal));
    }

    @PutMapping("/announcements/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Cập nhật thông báo",
            description = "Giảng viên chỉnh sửa nội dung một thông báo đã đăng."
    )
    public ApiResponse<AnnouncementResponse> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.updateAnnouncement(id, request, userPrincipal));
    }

    @DeleteMapping("/announcements/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    @Operation(
            summary = "Xóa thông báo",
            description = "Giảng viên xóa một thông báo đã đăng."
    )
    public ApiResponse<Void> deleteAnnouncement(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.deleteAnnouncement(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes/{classId}/announcements")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Lấy danh sách thông báo của lớp học phần",
            description = "Sinh viên/giảng viên thuộc lớp xem tất cả thông báo đã được đăng trong lớp."
    )
    public ApiResponse<List<AnnouncementResponse>> getAnnouncementsByClass(
            @PathVariable Long classId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.verifyAccessToClass(classId, userPrincipal);
        return ApiResponse.success(contentService.getAnnouncementsByClass(classId));
    }

    // ─── Video upload (Cloudinary) ───────────────────────────────────────────────

    private final com.ex.learninghub.modules.content.service.VideoUploadService videoUploadService;

    @PostMapping("/lessons/{id}/upload-video")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    @Operation(
            summary = "Upload video bài học lên Cloudinary",
            description = "Upload file video cho một bài học, lưu trên Cloudinary và trả về URL video."
    )
    public ApiResponse<String> uploadVideo(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam("file")
            org.springframework.web.multipart.MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String url = videoUploadService.uploadLessonVideo(id, file, userPrincipal);
        return ApiResponse.success(url);
    }
}
