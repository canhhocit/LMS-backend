package com.ex.learninghub.modules.content.service;

import com.ex.learninghub.modules.content.entity.VideoProgress;
import com.ex.learninghub.modules.content.repository.VideoProgressRepository;
import com.ex.learninghub.modules.enrollment.entity.Enrollment;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.content.entity.InVideoQuiz;
import com.ex.learninghub.modules.content.repository.InVideoQuizRepository;
import com.ex.learninghub.modules.content.entity.StudentVideoNote;
import com.ex.learninghub.modules.content.repository.StudentVideoNoteRepository;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.user.entity.User;
import com.ex.learninghub.modules.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoLearningService {

    private final VideoProgressRepository videoProgressRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final LessonRepository lessonRepo;
    private final InVideoQuizRepository quizRepo;
    private final StudentVideoNoteRepository noteRepo;
    private final UserRepository userRepo;

    @Transactional
    public VideoProgress upsertProgress(Long enrollmentId, Long lessonId, BigDecimal lastWatched, BigDecimal maxWatched) {
        Optional<VideoProgress> opt = videoProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lessonId);
        VideoProgress vp = opt.orElseGet(() -> {
            VideoProgress newVp = new VideoProgress();
            Enrollment enrollment = enrollmentRepo.getReferenceById(enrollmentId);
            Lesson lesson = lessonRepo.getReferenceById(lessonId);
            newVp.setEnrollment(enrollment);
            newVp.setLesson(lesson);
            return newVp;
        });
        // update progress values
        if (lastWatched.compareTo(vp.getLastWatchedSeconds()) > 0) {
            vp.setLastWatchedSeconds(lastWatched);
        }
        if (maxWatched.compareTo(vp.getMaxWatchedSeconds()) > 0) {
            vp.setMaxWatchedSeconds(maxWatched);
        }
        // determine completion (80% of lesson duration)
        Lesson lesson = vp.getLesson();
        if (lesson.getDuration() != null) {
            BigDecimal threshold = new BigDecimal(lesson.getDuration()).multiply(new BigDecimal("0.8"));
            if (vp.getMaxWatchedSeconds().compareTo(threshold) >= 0) {
                vp.setIsCompleted(true);
                // TODO: Mark lesson progress completed in existing LessonProgress service
            }
        }
        return videoProgressRepo.save(vp);
    }

    public List<InVideoQuiz> getQuizzesForLesson(Long lessonId) {
        return quizRepo.findByLessonIdOrderByTriggerAtSecondsAsc(lessonId);
    }

    public InVideoQuiz createQuiz(InVideoQuiz quiz) {
        return quizRepo.save(quiz);
    }

    public List<StudentVideoNote> getNotes(Long userId, Long lessonId) {
        return noteRepo.findByUserIdAndLessonIdOrderByTimestampSecondsAsc(userId, lessonId);
    }

    public StudentVideoNote addNote(Long userId, Long lessonId, String noteText, BigDecimal timestamp) {
        User user = userRepo.getReferenceById(userId);
        Lesson lesson = lessonRepo.getReferenceById(lessonId);
        StudentVideoNote note = StudentVideoNote.builder()
                .user(user)
                .lesson(lesson)
                .noteText(noteText)
                .timestampSeconds(timestamp)
                .build();
        return noteRepo.save(note);
    }

    public Optional<VideoProgress> getProgress(Long enrollmentId, Long lessonId) {
        return videoProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lessonId);
    }
}
