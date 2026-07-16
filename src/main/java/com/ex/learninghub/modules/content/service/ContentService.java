package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.request.AnnouncementRequest;
import com.ex.learninghub.modules.content.dto.request.ChapterRequest;
import com.ex.learninghub.modules.content.dto.request.LessonRequest;
import com.ex.learninghub.modules.content.dto.response.AnnouncementResponse;
import com.ex.learninghub.modules.content.dto.response.ChapterResponse;
import com.ex.learninghub.modules.content.dto.response.LessonResponse;

import java.util.List;

public interface ContentService {

    // Chapter
    ChapterResponse createChapter(Long classId, ChapterRequest request, UserPrincipal userPrincipal);

    ChapterResponse updateChapter(Long chapterId, ChapterRequest request, UserPrincipal userPrincipal);

    void deleteChapter(Long chapterId, UserPrincipal userPrincipal);

    List<ChapterResponse> getChaptersByClass(Long classId);

    // Lesson
    LessonResponse createLesson(Long chapterId, LessonRequest request, UserPrincipal userPrincipal);

    LessonResponse updateLesson(Long lessonId, LessonRequest request, UserPrincipal userPrincipal);

    void deleteLesson(Long lessonId, UserPrincipal userPrincipal);

    List<LessonResponse> getLessonsByChapter(Long chapterId);

    // Announcement
    AnnouncementResponse createAnnouncement(Long classId, AnnouncementRequest request, UserPrincipal userPrincipal);

    AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementRequest request, UserPrincipal userPrincipal);

    void deleteAnnouncement(Long announcementId, UserPrincipal userPrincipal);

    List<AnnouncementResponse> getAnnouncementsByClass(Long classId);
}
