package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.content.dto.request.InVideoQuizRequest;
import com.ex.learninghub.modules.content.dto.response.ManagedInVideoQuizResponse;
import com.ex.learninghub.modules.content.dto.response.StudentInVideoQuizResponse;
import com.ex.learninghub.modules.content.dto.response.VideoProgressResponse;
import com.ex.learninghub.modules.content.entity.InVideoQuiz;
import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import com.ex.learninghub.modules.content.entity.VideoProgress;
import com.ex.learninghub.modules.content.repository.InVideoQuizRepository;
import com.ex.learninghub.modules.content.repository.StudentVideoNoteRepository;
import com.ex.learninghub.modules.content.repository.VideoProgressRepository;
import com.ex.learninghub.modules.course.entity.Chapter;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ChapterRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.entity.LessonProgress;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.enrollment.repository.LessonProgressRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoLearningService {

    private static final BigDecimal COMPLETION_RATIO = new BigDecimal("0.8");

    private final VideoProgressRepository videoProgressRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final LessonRepository lessonRepo;
    private final ChapterRepository chapterRepo;
    private final ClazzRepository clazzRepo;
    private final InVideoQuizRepository quizRepo;
    private final StudentVideoNoteRepository noteRepo;
    private final UserRepository userRepo;
    private final LessonProgressRepository lessonProgressRepo;

    @Transactional
    public VideoProgressResponse upsertProgress(
            Long enrollmentId,
            Long lessonId,
            BigDecimal lastWatched,
            BigDecimal maxWatched,
            UserPrincipal userPrincipal) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        if (!enrollment.getStudent().getId().equals(userPrincipal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        verifyLessonBelongsToEnrollment(enrollment, lesson);
        validateProgressTimes(lesson, lastWatched, maxWatched);

        VideoProgress progress = videoProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> {
                    VideoProgress newProgress = new VideoProgress();
                    newProgress.setEnrollment(enrollment);
                    newProgress.setLesson(lesson);
                    return newProgress;
                });

        progress.setLastWatchedSeconds(lastWatched);
        if (maxWatched.compareTo(progress.getMaxWatchedSeconds()) > 0) {
            progress.setMaxWatchedSeconds(maxWatched);
        }

        if (hasReachedCompletionThreshold(lesson, progress.getMaxWatchedSeconds())) {
            progress.setIsCompleted(true);
            markLessonCompleted(enrollmentId, lessonId);
        }

        return VideoProgressResponse.from(videoProgressRepo.save(progress));
    }

    @Transactional(readOnly = true)
    public Optional<VideoProgressResponse> getProgress(Long enrollmentId, Long lessonId, UserPrincipal userPrincipal) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        if (!enrollment.getStudent().getId().equals(userPrincipal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        verifyLessonBelongsToEnrollment(enrollment, lesson);
        return videoProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .map(VideoProgressResponse::from);
    }

    @Transactional(readOnly = true)
    public List<StudentInVideoQuizResponse> getStudentQuizzes(Long lessonId, UserPrincipal principal) {
        Lesson lesson = requireLesson(lessonId);
        requireStudentLessonAccess(lesson, principal.getUser().getId());
        return quizRepo.findByLessonIdOrderByTriggerAtSecondsAsc(lessonId).stream()
                .map(StudentInVideoQuizResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ManagedInVideoQuizResponse> getManagedQuizzes(Long lessonId, UserPrincipal principal) {
        Lesson lesson = requireLesson(lessonId);
        verifyQuizManagementAccess(lesson, principal);
        return quizRepo.findByLessonIdOrderByTriggerAtSecondsAsc(lessonId).stream()
                .map(ManagedInVideoQuizResponse::from)
                .toList();
    }

    @Transactional
    public ManagedInVideoQuizResponse createQuiz(InVideoQuizRequest request, UserPrincipal principal) {
        Lesson lesson = requireLesson(request.lessonId());
        verifyQuizManagementAccess(lesson, principal);
        InVideoQuiz quiz = InVideoQuiz.builder()
                .lesson(lesson)
                .triggerAtSeconds(request.triggerAtSeconds())
                .questionText(request.questionText())
                .optionA(request.optionA())
                .optionB(request.optionB())
                .optionC(request.optionC())
                .optionD(request.optionD())
                .correctOption(request.correctOption())
                .build();
        return ManagedInVideoQuizResponse.from(quizRepo.save(quiz));
    }

    @Transactional(readOnly = true)
    public List<StudentVideoNote> getNotes(Long lessonId, UserPrincipal principal) {
        Lesson lesson = requireLesson(lessonId);
        requireStudentLessonAccess(lesson, principal.getUser().getId());
        return noteRepo.findByUserIdAndLessonIdOrderByTimestampSecondsAsc(principal.getUser().getId(), lessonId);
    }

    @Transactional
    public StudentVideoNote addNote(
            Long lessonId,
            String noteText,
            BigDecimal timestamp,
            UserPrincipal principal) {
        Lesson lesson = requireLesson(lessonId);
        requireStudentLessonAccess(lesson, principal.getUser().getId());
        if (timestamp == null || timestamp.signum() < 0
                || (lesson.getDuration() != null && lesson.getDuration() > 0
                && timestamp.compareTo(BigDecimal.valueOf(lesson.getDuration())) > 0)) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        User user = userRepo.getReferenceById(principal.getUser().getId());
        StudentVideoNote note = StudentVideoNote.builder()
                .user(user)
                .lesson(lesson)
                .noteText(noteText)
                .timestampSeconds(timestamp)
                .build();
        return noteRepo.save(note);
    }

    public void verifyLessonBelongsToEnrollment(Enrollment enrollment, Lesson lesson) {
        Chapter chapter = chapterRepo.findById(lesson.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        if (!chapter.getClazzId().equals(enrollment.getClazz().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    public void requireCompletionEligible(Long enrollmentId, Lesson lesson) {
        if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()) {
            return;
        }
        if (lesson.getDuration() == null || lesson.getDuration() <= 0) {
            throw new AppException(ErrorCode.VIDEO_DURATION_UNAVAILABLE);
        }
        VideoProgress progress = videoProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lesson.getId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_COMPLETED));
        if (!hasReachedCompletionThreshold(lesson, progress.getMaxWatchedSeconds())) {
            throw new AppException(ErrorCode.LESSON_NOT_COMPLETED);
        }
    }

    private Lesson requireLesson(Long lessonId) {
        return lessonRepo.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
    }

    private void requireStudentLessonAccess(Lesson lesson, Long studentId) {
        Chapter chapter = chapterRepo.findById(lesson.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        if (!enrollmentRepo.existsByStudentIdAndClazzId(studentId, chapter.getClazzId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void verifyQuizManagementAccess(Lesson lesson, UserPrincipal principal) {
        Role role = principal.getUser().getRole();
        if (role == Role.ADMIN) {
            return;
        }
        if (role != Role.LECTURER) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        Chapter chapter = chapterRepo.findById(lesson.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        Clazz clazz = clazzRepo.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
        if (clazz.getLecturer() == null
                || !clazz.getLecturer().getId().equals(principal.getUser().getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateProgressTimes(Lesson lesson, BigDecimal lastWatched, BigDecimal maxWatched) {
        if (lastWatched == null || maxWatched == null
                || lastWatched.signum() < 0 || maxWatched.signum() < 0
                || lastWatched.compareTo(maxWatched) > 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        if (lesson.getVideoUrl() == null || lesson.getVideoUrl().isBlank()
                || lesson.getDuration() == null || lesson.getDuration() <= 0) {
            throw new AppException(ErrorCode.VIDEO_DURATION_UNAVAILABLE);
        }
        BigDecimal duration = BigDecimal.valueOf(lesson.getDuration());
        if (lastWatched.compareTo(duration) > 0 || maxWatched.compareTo(duration) > 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
    }

    private boolean hasReachedCompletionThreshold(Lesson lesson, BigDecimal maxWatched) {
        if (lesson.getDuration() == null || lesson.getDuration() <= 0) {
            return false;
        }
        BigDecimal threshold = BigDecimal.valueOf(lesson.getDuration()).multiply(COMPLETION_RATIO);
        return maxWatched.compareTo(threshold) >= 0;
    }

    private void markLessonCompleted(Long enrollmentId, Long lessonId) {
        LessonProgress progress = lessonProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    newProgress.setEnrollment(enrollmentRepo.getReferenceById(enrollmentId));
                    newProgress.setLesson(lessonRepo.getReferenceById(lessonId));
                    return newProgress;
                });
        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepo.save(progress);
    }
}
