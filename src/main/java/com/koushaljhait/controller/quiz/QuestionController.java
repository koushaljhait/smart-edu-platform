package com.koushaljhait.controller.quiz;

import com.koushaljhait.model.quiz.Question;
import com.koushaljhait.service.quiz.QuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
public class QuestionController {
    
    private final QuestionService questionService;
    
    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    
    // Get all questions for a quiz (teacher views questions)
    @GetMapping
    public ResponseEntity<List<Question>> getQuizQuestions(@PathVariable Long quizId) {
        try {
            List<Question> questions = questionService.getQuestionsForQuiz(quizId);
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Get a specific question
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long quizId, 
                                                   @PathVariable Long questionId) {
        Optional<Question> question = questionService.getQuestionById(questionId);
        if (question.isPresent() && question.get().getQuizId().equals(quizId)) {
            return ResponseEntity.ok(question.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    // Add a single question to a quiz (teacher adds question)
    @PostMapping
    public ResponseEntity<?> addQuestionToQuiz(@PathVariable Long quizId, 
                                              @RequestBody Question question) {
        try {
            Question createdQuestion = questionService.addQuestionToQuiz(quizId, question);
            return ResponseEntity.ok(createdQuestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Add multiple questions at once (bulk upload)
    @PostMapping("/bulk")
    public ResponseEntity<?> addQuestionsToQuiz(@PathVariable Long quizId,
                                               @RequestBody List<Question> questions) {
        try {
            List<Question> createdQuestions = questionService.addQuestionsToQuiz(quizId, questions);
            return ResponseEntity.ok(createdQuestions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Update a question
    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long quizId,
                                           @PathVariable Long questionId,
                                           @RequestBody Question questionDetails) {
        try {
            // Verify the question belongs to the quiz
            Optional<Question> existingQuestion = questionService.getQuestionById(questionId);
            if (existingQuestion.isEmpty() || !existingQuestion.get().getQuizId().equals(quizId)) {
                return ResponseEntity.notFound().build();
            }
            
            Question updatedQuestion = questionService.updateQuestion(questionId, questionDetails);
            return ResponseEntity.ok(updatedQuestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Delete a question
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long quizId,
                                           @PathVariable Long questionId) {
        try {
            // Verify the question belongs to the quiz
            Optional<Question> existingQuestion = questionService.getQuestionById(questionId);
            if (existingQuestion.isEmpty() || !existingQuestion.get().getQuizId().equals(quizId)) {
                return ResponseEntity.notFound().build();
            }
            
            questionService.deleteQuestion(questionId);
            return ResponseEntity.ok().body("Question deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Delete all questions for a quiz
    @DeleteMapping
    public ResponseEntity<?> deleteAllQuestions(@PathVariable Long quizId) {
        try {
            questionService.deleteAllQuestionsForQuiz(quizId);
            return ResponseEntity.ok().body("All questions deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Reorder questions in a quiz
    @PostMapping("/reorder")
    public ResponseEntity<?> reorderQuestions(@PathVariable Long quizId,
                                             @RequestBody List<Long> questionIdsInOrder) {
        try {
            questionService.reorderQuestions(quizId, questionIdsInOrder);
            return ResponseEntity.ok().body("Questions reordered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Get question count for a quiz
    @GetMapping("/count")
    public ResponseEntity<Long> getQuestionCount(@PathVariable Long quizId) {
        try {
            Long count = questionService.getQuestionCountForQuiz(quizId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(0L);
        }
    }
    
    // Get total marks for a quiz
    @GetMapping("/total-marks")
    public ResponseEntity<Integer> getTotalMarks(@PathVariable Long quizId) {
        try {
            Integer totalMarks = questionService.getTotalMarksForQuiz(quizId);
            return ResponseEntity.ok(totalMarks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(0);
        }
    }
    
    // Search questions within a quiz
    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(@PathVariable Long quizId,
                                                         @RequestParam String q) {
        try {
            List<Question> questions = questionService.searchQuestionsInQuiz(quizId, q);
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}