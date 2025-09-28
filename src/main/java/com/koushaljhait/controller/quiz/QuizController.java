package com.koushaljhait.controller.quiz;

import com.koushaljhait.model.quiz.Quiz;
import com.koushaljhait.service.quiz.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    
    private final QuizService quizService;
    
    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    
    // Get all quizzes for a teacher (teacher dashboard)
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Quiz>> getTeacherQuizzes(@PathVariable Long teacherId) {
        List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
        return ResponseEntity.ok(quizzes);
    }
    
    // Get all published quizzes (for students)
    @GetMapping("/published")
    public ResponseEntity<List<Quiz>> getPublishedQuizzes() {
        List<Quiz> quizzes = quizService.getPublishedQuizzes();
        return ResponseEntity.ok(quizzes);
    }
    
    // Get published quizzes by subject
    @GetMapping("/published/subject/{subjectId}")
    public ResponseEntity<List<Quiz>> getPublishedQuizzesBySubject(@PathVariable Long subjectId) {
        List<Quiz> quizzes = quizService.getPublishedQuizzesBySubject(subjectId);
        return ResponseEntity.ok(quizzes);
    }
    
    // Get quiz by ID
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // Create a new quiz (teacher creates quiz)
    @PostMapping
    public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz) {
        try {
            Quiz createdQuiz = quizService.createQuiz(quiz);
            return ResponseEntity.ok(createdQuiz);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Update quiz details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        try {
            Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
            return ResponseEntity.ok(updatedQuiz);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Publish a quiz (make it available to students)
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishQuiz(@PathVariable Long id) {
        try {
            Quiz publishedQuiz = quizService.publishQuiz(id);
            return ResponseEntity.ok(publishedQuiz);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Unpublish a quiz
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishQuiz(@PathVariable Long id) {
        try {
            Quiz unpublishedQuiz = quizService.unpublishQuiz(id);
            return ResponseEntity.ok(unpublishedQuiz);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Delete a quiz
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.ok().body("Quiz deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Search quizzes by title
    @GetMapping("/search")
    public ResponseEntity<List<Quiz>> searchQuizzes(@RequestParam String title) {
        List<Quiz> quizzes = quizService.searchQuizzes(title);
        return ResponseEntity.ok(quizzes);
    }
    
    // Get quiz count for teacher
    @GetMapping("/teacher/{teacherId}/count")
    public ResponseEntity<Long> getQuizCount(@PathVariable Long teacherId) {
        Long count = quizService.getQuizCountByTeacher(teacherId);
        return ResponseEntity.ok(count);
    }
}