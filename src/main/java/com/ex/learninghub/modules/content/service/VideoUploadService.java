package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoUploadService {

    private final Cloudinary cloudinary;
    private final LessonRepository lessonRepository;
    private final com.ex.learninghub.modules.course.repository.ClazzRepository clazzRepository;

    @Value("${app.upload.max-video-size:200MB}")
    private org.springframework.util.unit.DataSize maxVideoSize;

    private static final java.util.Set<String> ALLOWED_TYPES = java.util.Set.of(
            "video/mp4", "video/webm", "video/quicktime", "video/x-msvideo", "video/x-matroska");

    /**
     * Upload a video for a lesson to Cloudinary and store the secure URL on the lesson.
     * Only the lecturer who owns the class (or an admin) may upload.
     */
    @Transactional
    public String uploadLessonVideo(Long lessonId, MultipartFile file, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Verify ownership via chapter -> clazz -> lecturer
        Long chapterId = lesson.getChapterId();
        // Resolve clazz through chapter -> clazzId
        com.ex.learninghub.modules.course.entity.Chapter chapterEntity =
                chapterRepository.findById(chapterId)
                        .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        var clazz = clazzRepository.findById(chapterEntity.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        boolean isAdmin = principal.getUser().getRole() == com.ex.learninghub.common.enums.Role.ADMIN;
        boolean isOwner = clazz.getLecturer() != null
                && clazz.getLecturer().getId().equals(principal.getUser().getId());
        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.VIDEO_FILE_EMPTY);
        }
        if (file.getSize() > maxVideoSize.toBytes()) {
            throw new AppException(ErrorCode.VIDEO_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.VIDEO_INVALID_FORMAT);
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "video"));
            String secureUrl = (String) result.get("secure_url");
            lesson.setVideoUrl(secureUrl);
            lessonRepository.save(lesson);
            return secureUrl;
        } catch (IOException ex) {
            throw new AppException(ErrorCode.VIDEO_UPLOAD_FAILED);
        }
    }

    private final com.ex.learninghub.modules.course.repository.ChapterRepository chapterRepository;
}
