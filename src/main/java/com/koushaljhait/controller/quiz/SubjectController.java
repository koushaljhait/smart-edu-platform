package com.koushaljhait.controller.quiz;

import com.koushaljhait.model.quiz.Subject;
import com.koushaljhait.service.quiz.SubjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    
    private final SubjectService subjectService;
    
    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }
    
    // Get all active subjects (for dropdowns in teacher UI)
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllActiveSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Get subject by ID
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Optional<Subject> subject = subjectService.getSubjectById(id);
        return subject.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // Create a new subject (admin functionality)
    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Subject subject) {
        try {
            Subject createdSubject = subjectService.createSubject(subject);
            return ResponseEntity.ok(createdSubject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Update subject
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody Subject subjectDetails) {
        try {
            Subject updatedSubject = subjectService.updateSubject(id, subjectDetails);
            return ResponseEntity.ok(updatedSubject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Deactivate subject
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateSubject(@PathVariable Long id) {
        try {
            subjectService.deactivateSubject(id);
            return ResponseEntity.ok().body("Subject deactivated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Search subjects by name
    @GetMapping("/search")
    public ResponseEntity<List<Subject>> searchSubjects(@RequestParam String name) {
        List<Subject> subjects = subjectService.searchSubjects(name);
        return ResponseEntity.ok(subjects);
    }
}