package com.koushaljhait.repository.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.koushaljhait.model.quiz.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Find all questions for a quiz
    List<Question> findByQuizId(Long quizId);
    
    // Find questions by quiz ID with ordering
    List<Question> findByQuizIdOrderByQuestionOrderAsc(Long quizId);
    
    // Count questions in a quiz
    Long countByQuizId(Long quizId);
    
    // Find questions by marks value
    List<Question> findByQuizIdAndMarks(Long quizId, Integer marks);
    
    // Custom query to calculate total marks for a quiz
    @Query("SELECT SUM(q.marks) FROM Question q WHERE q.quizId = :quizId")
    Integer calculateTotalMarksForQuiz(@Param("quizId") Long quizId);
    
    // Find questions containing specific text
    @Query("SELECT q FROM Question q WHERE q.quizId = :quizId AND LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Question> searchQuestionsInQuiz(
        @Param("quizId") Long quizId,
        @Param("searchText") String searchText
    );
    
    // Get next question order number for a quiz
    @Query("SELECT COALESCE(MAX(q.questionOrder), 0) + 1 FROM Question q WHERE q.quizId = :quizId")
    Integer getNextQuestionOrder(@Param("quizId") Long quizId);
    
    // Delete all questions for a quiz
    void deleteByQuizId(Long quizId);
}