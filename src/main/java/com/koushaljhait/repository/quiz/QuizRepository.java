package com.koushaljhait.repository.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.koushaljhait.model.quiz.Quiz;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    // Find all quizzes by teacher
    List<Quiz> findByTeacherId(Long teacherId);
    
    // Find all published quizzes
    List<Quiz> findByIsPublishedTrue();
    
    // Find quizzes by subject
    List<Quiz> findBySubjectId(Long subjectId);
    
    // Find published quizzes by subject
    List<Quiz> findBySubjectIdAndIsPublishedTrue(Long subjectId);
    
    // Find quizzes by title (search)
    List<Quiz> findByTitleContainingIgnoreCase(String title);
    
    // Count quizzes by teacher
    Long countByTeacherId(Long teacherId);
    
    // Custom query to find quizzes with question count
    @Query("SELECT q FROM Quiz q WHERE q.isPublished = true ORDER BY q.createdAt DESC")
    List<Quiz> findRecentPublishedQuizzes();
    
    // Find quizzes created by teacher in a date range
    @Query("SELECT q FROM Quiz q WHERE q.teacherId = :teacherId AND q.createdAt BETWEEN :startDate AND :endDate")
    List<Quiz> findTeacherQuizzesInDateRange(
        @Param("teacherId") Long teacherId,
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate
    );
}