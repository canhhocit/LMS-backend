package com.ex.learninghub.modules.content.service.impl;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.request.AnnouncementRequest;
import com.ex.learninghub.modules.content.dto.request.ChapterRequest;
import com.ex.learninghub.modules.content.dto.request.LessonRequest;
import com.ex.learninghub.modules.content.dto.response.AnnouncementResponse;
import com.ex.learninghub.modules.content.dto.response.ChapterResponse;
import com.ex.learninghub.modules.content.dto.response.LessonResponse;
import com.ex.learninghub.modules.content.entity.Announcement;
import com.ex.learninghub.modules.content.repository.AnnouncementRepository;
import com.ex.learninghub.modules.content.service.ContentService;
import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.modules.course.entity.Chapter;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ChapterRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ClazzRepository clazzRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final AnnouncementRepository announcementRepository;
    private final com.ex.learninghub.modules.notification.service.NotificationService notificationService;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void verifyAccessToClass(Long classId, UserPrincipal userPrincipal) {
        if (userPrincipal.getUser().getRole() == Role.ADMIN) return;
        if (userPrincipal.getUser().getRole() == Role.LECTURER) {
            Clazz clazz = clazzRepository.findById(classId)
                    .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
            if (clazz.getLecturer() != null && clazz.getLecturer().getId().equals(userPrincipal.getUser().getId())) return;
        }
        if (!enrollmentRepository.existsByStudentIdAndClazzId(userPrincipal.getUser().getId(), classId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    public void verifyAccessToChapter(Long chapterId, UserPrincipal userPrincipal) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        verifyAccessToClass(chapter.getClazzId(), userPrincipal);
    }

    private void verifyLecturerOwnsClazz(Clazz clazz, UserPrincipal userPrincipal) {
        if (clazz.getLecturer() == null ||
                !clazz.getLecturer().getId().equals(userPrincipal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public ChapterResponse createChapter(Long classId, ChapterRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        Chapter chapter = Chapter.builder()
                .clazzId(classId)
                .title(request.getTitle())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
        return ChapterResponse.from(chapterRepository.save(chapter));
    }

    @Override
    @Transactional
    public ChapterResponse updateChapter(Long chapterId, ChapterRequest request, UserPrincipal userPrincipal) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        chapter.setTitle(request.getTitle());
        chapter.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : chapter.getSortOrder());
        return ChapterResponse.from(chapterRepository.save(chapter));
    }

    @Override
    @Transactional
    public void deleteChapter(Long chapterId, UserPrincipal userPrincipal) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        chapterRepository.delete(chapter);
    }

    @Override
    public List<ChapterResponse> getChaptersByClass(Long classId) {
        return chapterRepository.findByClazzIdOrderBySortOrderAsc(classId).stream()
                .map(ChapterResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChapterResponse> getChaptersByClass(Long classId, Pageable pageable) {
        return chapterRepository.findByClazzId(classId, pageable).map(ChapterResponse::from);
    }

    @Override
    @Transactional
    public LessonResponse createLesson(Long chapterId, LessonRequest request, UserPrincipal userPrincipal) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        Lesson lesson = Lesson.builder()
                .chapterId(chapterId)
                .title(request.getTitle())
                .content(request.getContent())
                .videoUrl(request.getVideoUrl())
                .build();
        return LessonResponse.from(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonRequest request, UserPrincipal userPrincipal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        Chapter chapter = chapterRepository.findById(lesson.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        return LessonResponse.from(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId, UserPrincipal userPrincipal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        Chapter chapter = chapterRepository.findById(lesson.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Clazz clazz = clazzRepository.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        lessonRepository.delete(lesson);
    }

    @Override
    public List<LessonResponse> getLessonsByChapter(Long chapterId) {
        return lessonRepository.findByChapterIdOrderBySortOrderAsc(chapterId).stream()
                .map(LessonResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LessonResponse> getLessonsByChapter(Long chapterId, Pageable pageable) {
        return lessonRepository.findByChapterId(chapterId, pageable).map(LessonResponse::from);
    }

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(Long classId, AnnouncementRequest request, UserPrincipal userPrincipal) {
        Clazz clazz = clazzRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        verifyLecturerOwnsClazz(clazz, userPrincipal);
        Announcement announcement = Announcement.builder()
                .clazz(clazz)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        var savedAnnouncement = announcementRepository.save(announcement);

        return AnnouncementResponse.from(savedAnnouncement);
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long announcementId, AnnouncementRequest request, UserPrincipal userPrincipal) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AppException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));
        verifyLecturerOwnsClazz(announcement.getClazz(), userPrincipal);
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        var savedAnnouncement = announcementRepository.save(announcement);

        // Notify all enrolled students about the updated announcement (WebSocket + DB)
        notificationService.notifyClazz(announcement.getClazz().getId(),
                com.ex.learninghub.common.enums.NotificationType.NEW_ANNOUNCEMENT,
                "Updated announcement: " + request.getTitle(),
                request.getContent(),
                savedAnnouncement.getId());

        return AnnouncementResponse.from(savedAnnouncement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId, UserPrincipal userPrincipal) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new AppException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));
        verifyLecturerOwnsClazz(announcement.getClazz(), userPrincipal);
        announcementRepository.delete(announcement);
    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsByClass(Long classId) {
        return announcementRepository.findByClazzIdOrderByCreatedAtDesc(classId).stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList());
    }
}