package com.koushaljhait.repository.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.koushaljhait.model.quiz.Subject;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    // Find subject by code
    Optional<Subject> findByCode(String code);
    
    // Find all active subjects
    List<Subject> findByIsActiveTrue();
    
    // Find subjects by name (case-insensitive search)
    List<Subject> findByNameContainingIgnoreCase(String name);
    
    // Check if subject code exists
    boolean existsByCode(String code);
    
    // Custom query to find subjects with quiz count
    @Query("SELECT s FROM Subject s WHERE s.isActive = true ORDER BY s.name")
    List<Subject> findAllActiveSubjects();
    
    // Find subjects created after a certain date
    @Query("SELECT s FROM Subject s WHERE s.createdAt >= :date")
    List<Subject> findSubjectsCreatedAfter(@Param("date") java.time.LocalDateTime date);
}