package com.koushaljhait.service.quiz;

import com.koushaljhait.model.quiz.Quiz;
import com.koushaljhait.repository.quiz.QuizRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    
    private final QuizRepository quizRepository;
    private final SubjectService subjectService;
    
    @Autowired
    public QuizService(QuizRepository quizRepository, SubjectService subjectService) {
        this.quizRepository = quizRepository;
        this.subjectService = subjectService;
    }
    
    // Create a new quiz
    public Quiz createQuiz(Quiz quiz) {
        // Validate that subject exists and is active
        if (!subjectService.isSubjectActive(quiz.getSubjectId())) {
            throw new IllegalArgumentException("Subject is not active or does not exist");
        }
        
        // Set default values if not provided
        if (quiz.getTotalMarks() == null) {
            quiz.setTotalMarks(0); // Will be calculated when questions are added
        }
        
        if (quiz.getTimeLimitMinutes() == null) {
            quiz.setTimeLimitMinutes(30); // Default 30 minutes
        }
        
        return quizRepository.save(quiz);
    }
    
    // Get quiz by ID
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    // Get all quizzes for a teacher
    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        return quizRepository.findByTeacherId(teacherId);
    }
    
    // Get all published quizzes (for students)
    public List<Quiz> getPublishedQuizzes() {
        return quizRepository.findRecentPublishedQuizzes();
    }
    
    // Get published quizzes by subject
    public List<Quiz> getPublishedQuizzesBySubject(Long subjectId) {
        return quizRepository.findBySubjectIdAndIsPublishedTrue(subjectId);
    }
    
    // Publish a quiz (make it available to students)
    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + quizId));
        
        quiz.setIsPublished(true);
        return quizRepository.save(quiz);
    }
    
    // Unpublish a quiz
    public Quiz unpublishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + quizId));
        
        quiz.setIsPublished(false);
        return quizRepository.save(quiz);
    }
    
    // Update quiz details
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));
        
        // Validate subject if changing
        if (!quiz.getSubjectId().equals(quizDetails.getSubjectId()) && 
            !subjectService.isSubjectActive(quizDetails.getSubjectId())) {
            throw new IllegalArgumentException("Subject is not active or does not exist");
        }
        
        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setSubjectId(quizDetails.getSubjectId());
        quiz.setTotalMarks(quizDetails.getTotalMarks());
        quiz.setTimeLimitMinutes(quizDetails.getTimeLimitMinutes());
        
        return quizRepository.save(quiz);
    }
    
    // Delete quiz
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
    
    // Search quizzes by title
    public List<Quiz> searchQuizzes(String title) {
        return quizRepository.findByTitleContainingIgnoreCase(title);
    }
    
    // Get quiz count for teacher
    public Long getQuizCountByTeacher(Long teacherId) {
        return quizRepository.countByTeacherId(teacherId);
    }
}