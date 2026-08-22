package com.ex.learninghub.modules.quiz.controller;

import com.ex.learninghub.modules.assessment.entity.Question;
import com.ex.learninghub.modules.assessment.entity.Quiz;
import com.ex.learninghub.modules.quiz.dto.QuizAttemptRequest;
import com.ex.learninghub.modules.quiz.dto.QuizAttemptResponse;
import com.ex.learninghub.modules.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // Quiz CRUD - Lecturer only
    @PostMapping("/class/{classId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Quiz> createQuiz(@PathVariable Long classId, @RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizService.createQuiz(classId, quiz));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Quiz>> getQuizzesByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(quizService.getQuizzesByClassId(classId));
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long quizId, @RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, quiz));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    // Question CRUD - Lecturer only
    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Question> createQuestion(@PathVariable Long quizId, @RequestBody Question question) {
        return ResponseEntity.ok(quizService.createQuestion(quizId, question));
    }

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionsByQuizId(quizId));
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long questionId, @RequestBody Question question) {
        return ResponseEntity.ok(quizService.updateQuestion(questionId, question));
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        quizService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // Quiz Attempt - Student only
    @PostMapping("/{quizId}/attempts")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAttemptResponse> submitAttempt(@PathVariable Long quizId, @RequestBody QuizAttemptRequest request) {
        return ResponseEntity.ok(quizService.submitAttempt(quizId, request));
    }
}
