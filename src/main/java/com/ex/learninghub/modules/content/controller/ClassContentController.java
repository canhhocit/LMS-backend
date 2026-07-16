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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClassContentController {

    private final ContentService contentService;

    // ─── Chapters ────────────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/chapters")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<ChapterResponse> createChapter(@PathVariable Long classId,
                                                        @Valid @RequestBody ChapterRequest request,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.createChapter(classId, request, userPrincipal));
    }

    @PutMapping("/chapters/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<ChapterResponse> updateChapter(@PathVariable Long id,
                                                       @Valid @RequestBody ChapterRequest request,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.updateChapter(id, request, userPrincipal));
    }

    @DeleteMapping("/chapters/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<Void> deleteChapter(@PathVariable Long id,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.deleteChapter(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes/{classId}/chapters")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ChapterResponse>> getChaptersByClass(@PathVariable Long classId) {
        return ApiResponse.success(contentService.getChaptersByClass(classId));
    }

    // ─── Lessons ─────────────────────────────────────────────────────────────────

    @PostMapping("/chapters/{chapterId}/lessons")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<LessonResponse> createLesson(@PathVariable Long chapterId,
                                                      @Valid @RequestBody LessonRequest request,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.createLesson(chapterId, request, userPrincipal));
    }

    @PutMapping("/lessons/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<LessonResponse> updateLesson(@PathVariable Long id,
                                                     @Valid @RequestBody LessonRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.updateLesson(id, request, userPrincipal));
    }

    @DeleteMapping("/lessons/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<Void> deleteLesson(@PathVariable Long id,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.deleteLesson(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/chapters/{chapterId}/lessons")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<LessonResponse>> getLessonsByChapter(@PathVariable Long chapterId) {
        return ApiResponse.success(contentService.getLessonsByChapter(chapterId));
    }

    // ─── Announcements ───────────────────────────────────────────────────────────

    @PostMapping("/classes/{classId}/announcements")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<AnnouncementResponse> createAnnouncement(@PathVariable Long classId,
                                                                  @Valid @RequestBody AnnouncementRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.createAnnouncement(classId, request, userPrincipal));
    }

    @PutMapping("/announcements/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<AnnouncementResponse> updateAnnouncement(@PathVariable Long id,
                                                                  @Valid @RequestBody AnnouncementRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(contentService.updateAnnouncement(id, request, userPrincipal));
    }

    @DeleteMapping("/announcements/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contentService.deleteAnnouncement(id, userPrincipal);
        return ApiResponse.success(null);
    }

    @GetMapping("/classes/{classId}/announcements")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<AnnouncementResponse>> getAnnouncementsByClass(@PathVariable Long classId) {
        return ApiResponse.success(contentService.getAnnouncementsByClass(classId));
    }
}
