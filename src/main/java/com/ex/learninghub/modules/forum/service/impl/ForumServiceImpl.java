package com.ex.learninghub.modules.forum.service.impl;

import com.ex.learninghub.common.enums.Role;
import com.ex.learninghub.common.exception.AppException;
import com.ex.learninghub.common.exception.ErrorCode;
import com.ex.learninghub.common.security.UserPrincipal;
import com.ex.learninghub.modules.course.entity.Clazz;
import com.ex.learninghub.modules.course.repository.ClazzRepository;
import com.ex.learninghub.modules.enrollment.repository.EnrollmentRepository;
import com.ex.learninghub.modules.forum.dto.request.ForumCommentRequest;
import com.ex.learninghub.modules.forum.dto.request.ForumPostRequest;
import com.ex.learninghub.modules.forum.dto.response.ForumResponse;
import com.ex.learninghub.modules.forum.entity.ForumComment;
import com.ex.learninghub.modules.forum.entity.ForumPost;
import com.ex.learninghub.modules.forum.repository.ForumCommentRepository;
import com.ex.learninghub.modules.forum.repository.ForumPostRepository;
import com.ex.learninghub.modules.forum.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final ForumPostRepository forumPostRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final ClazzRepository clazzRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public ForumResponse.Post createPost(Long clazzId, ForumPostRequest request, UserPrincipal principal) {
        Clazz clazz = verifyClazzMember(clazzId, principal);
        if (principal.getUser().getRole() == Role.STUDENT) {
            // Students must be enrolled to post; lecturers must own the class (checked in verifyClazzMember)
            boolean enrolled = enrollmentRepository.existsByStudentIdAndClazzId(
                    principal.getUser().getId(), clazzId);
            if (!enrolled) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }

        ForumPost post = ForumPost.builder()
                .clazz(clazz)
                .author(principal.getUser())
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return ForumResponse.Post.from(forumPostRepository.save(post));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumResponse.Post> getPostsByClazz(Long clazzId, UserPrincipal principal) {
        verifyClazzMember(clazzId, principal);
        return forumPostRepository.findByClazzIdOrderByCreatedAtDesc(clazzId).stream()
                .map(ForumResponse.Post::from)
                .toList();
    }

    @Override
    @Transactional
    public void deletePost(Long postId, UserPrincipal principal) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.FORUM_POST_NOT_FOUND));

        Long userId = principal.getUser().getId();
        Role role = principal.getUser().getRole();

        boolean isAuthor = post.getAuthor().getId().equals(userId);
        boolean isClassLecturer = post.getClazz().getLecturer() != null
                && post.getClazz().getLecturer().getId().equals(userId);

        if (!isAuthor && !isClassLecturer && role != Role.ADMIN) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        forumCommentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .forEach(forumCommentRepository::delete);
        forumPostRepository.delete(post);
    }

    @Override
    @Transactional
    public ForumResponse.Comment addComment(Long postId, ForumCommentRequest request, UserPrincipal principal) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.FORUM_POST_NOT_FOUND));
        verifyClazzMember(post.getClazz().getId(), principal);

        ForumComment comment = ForumComment.builder()
                .post(post)
                .author(principal.getUser())
                .content(request.getContent())
                .build();
        return ForumResponse.Comment.from(forumCommentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumResponse.Comment> getComments(Long postId, UserPrincipal principal) {
        ForumPost post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.FORUM_POST_NOT_FOUND));
        verifyClazzMember(post.getClazz().getId(), principal);
        return forumCommentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(ForumResponse.Comment::from)
                .toList();
    }

    /**
     * Verify the user is a member of the clazz: enrolled student, class lecturer, or admin.
     */
    private Clazz verifyClazzMember(Long clazzId, UserPrincipal principal) {
        Clazz clazz = clazzRepository.findById(clazzId)
                .orElseThrow(() -> new AppException(ErrorCode.CLAZZ_NOT_FOUND));

        Role role = principal.getUser().getRole();
        if (role == Role.ADMIN) {
            return clazz;
        }
        Long userId = principal.getUser().getId();

        if (role == Role.LECTURER
                && (clazz.getLecturer() == null || !clazz.getLecturer().getId().equals(userId))) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (role == Role.LECTURER) {
            return clazz;
        }
        if (!enrollmentRepository.existsByStudentIdAndClazzId(userId, clazzId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        return clazz;
    }
}
