package com.koushaljhait.service.quiz;

import com.koushaljhait.model.quiz.Subject;
import com.koushaljhait.repository.quiz.SubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    
    private final SubjectRepository subjectRepository;
    
    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
    
    // Create a new subject
    public Subject createSubject(Subject subject) {
        // Validate subject code doesn't exist
        if (subjectRepository.existsByCode(subject.getCode())) {
            throw new IllegalArgumentException("Subject code already exists: " + subject.getCode());
        }
        
        return subjectRepository.save(subject);
    }
    
    // Get all active subjects (for dropdowns in UI)
    public List<Subject> getAllActiveSubjects() {
        return subjectRepository.findAllActiveSubjects();
    }
    
    // Get subject by ID
    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }
    
    // Get subject by code
    public Optional<Subject> getSubjectByCode(String code) {
        return subjectRepository.findByCode(code);
    }
    
    // Update subject
    public Subject updateSubject(Long id, Subject subjectDetails) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found with id: " + id));
        
        // Check if code is being changed and if new code already exists
        if (!subject.getCode().equals(subjectDetails.getCode()) && 
            subjectRepository.existsByCode(subjectDetails.getCode())) {
            throw new IllegalArgumentException("Subject code already exists: " + subjectDetails.getCode());
        }
        
        subject.setName(subjectDetails.getName());
        subject.setCode(subjectDetails.getCode());
        subject.setDescription(subjectDetails.getDescription());
        subject.setIsActive(subjectDetails.getIsActive());
        
        return subjectRepository.save(subject);
    }
    
    // Soft delete (deactivate) subject
    public void deactivateSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found with id: " + id));
        
        subject.setIsActive(false);
        subjectRepository.save(subject);
    }
    
    // Search subjects by name
    public List<Subject> searchSubjects(String name) {
        return subjectRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Check if subject exists and is active
    public boolean isSubjectActive(Long subjectId) {
        return subjectRepository.findById(subjectId)
            .map(Subject::getIsActive)
            .orElse(false);
    }
}