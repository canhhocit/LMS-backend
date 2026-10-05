package com.ex.learninghub.modules.content.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.modules.content.dto.request.LessonCommentRequest;
import com.ex.learninghub.modules.content.dto.response.LessonCommentResponse;
import com.ex.learninghub.modules.content.entity.LessonComment;
import com.ex.learninghub.modules.content.repository.LessonCommentRepository;
import com.ex.learninghub.modules.content.service.LessonCommentService;
import com.ex.learninghub.modules.course.entity.Chapter;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.entity.Lesson;
import com.ex.learninghub.modules.course.repository.ChapterRepository;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.course.repository.LessonRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.user.repository.UserRepository;
import com.ex.learninghub.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonCommentServiceImpl implements LessonCommentService {

    private final LessonCommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final ClazzRepository clazzRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    private Clazz getClazzByLesson(Lesson lesson) {
        Chapter chapter = chapterRepository.findById(lesson.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        return clazzRepository.findById(chapter.getClazzId())
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));
    }

    private void checkAccess(Lesson lesson, UserPrincipal principal) {
        if (principal.getUser().getRole() == Role.ADMIN) return;
        
        Clazz clazz = getClazzByLesson(lesson);
        
        if (principal.getUser().getRole() == Role.LECTURER) {
            if (clazz.getLecturer() != null && clazz.getLecturer().getId().equals(principal.getUser().getId())) {
                return;
            }
        } else if (principal.getUser().getRole() == Role.STUDENT) {
            if (enrollmentRepository.existsByStudentIdAndClazzId(principal.getUser().getId(), clazz.getId())) {
                return;
            }
        }
        throw new AppException(ErrorCode.FORBIDDEN);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonCommentResponse> getComments(Long lessonId, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        checkAccess(lesson, principal);
        
        return commentRepository.findByLessonIdOrderByCreatedAtDesc(lessonId)
                .stream().map(LessonCommentResponse::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LessonCommentResponse addComment(Long lessonId, LessonCommentRequest request, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        checkAccess(lesson, principal);

        LessonComment comment = LessonComment.builder()
                .lessonId(lessonId)
                .user(userRepository.getReferenceById(principal.getUser().getId()))
                .content(request.getContent())
                .parentId(request.getParentId())
                .build();
        
        comment = commentRepository.save(comment);
        // Load entity to fetch user details correctly for response
        comment.setUser(userRepository.findById(principal.getUser().getId()).orElseThrow());
        
        return LessonCommentResponse.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, UserPrincipal principal) {
        LessonComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
                
        // Only owner or admin can delete
        if (!comment.getUser().getId().equals(principal.getUser().getId()) 
                && principal.getUser().getRole() != Role.ADMIN) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        
        commentRepository.delete(comment);
    }
}
