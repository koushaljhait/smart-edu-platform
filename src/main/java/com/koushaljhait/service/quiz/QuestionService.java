package com.koushaljhait.service.quiz;

import com.koushaljhait.model.quiz.Question;
import com.koushaljhait.model.quiz.Quiz;
import com.koushaljhait.repository.quiz.QuestionRepository;
import com.koushaljhait.repository.quiz.QuizRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuizService quizService;
    
    @Autowired
    public QuestionService(QuestionRepository questionRepository, 
                          QuizRepository quizRepository,
                          QuizService quizService) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.quizService = quizService;
    }
    
    // Add a new question to a quiz
    public Question addQuestionToQuiz(Long quizId, Question question) {
        // Verify quiz exists
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + quizId));
        
        // Set quiz ID and auto-generate order if not provided
        question.setQuizId(quizId);
        if (question.getQuestionOrder() == null) {
            question.setQuestionOrder(questionRepository.getNextQuestionOrder(quizId));
        }
        
        // Validate correct answer
        validateCorrectAnswer(question);
        
        Question savedQuestion = questionRepository.save(question);
        
        // Update quiz total marks
        updateQuizTotalMarks(quizId);
        
        return savedQuestion;
    }
    
    // Add multiple questions at once
    public List<Question> addQuestionsToQuiz(Long quizId, List<Question> questions) {
        // Verify quiz exists
        if (!quizRepository.existsById(quizId)) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }
        
        // Set quiz ID and order for each question
        Integer nextOrder = questionRepository.getNextQuestionOrder(quizId);
        for (Question question : questions) {
            question.setQuizId(quizId);
            if (question.getQuestionOrder() == null) {
                question.setQuestionOrder(nextOrder++);
            }
            validateCorrectAnswer(question);
        }
        
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        
        // Update quiz total marks
        updateQuizTotalMarks(quizId);
        
        return savedQuestions;
    }
    
    // Get all questions for a quiz (in order)
    public List<Question> getQuestionsForQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }
        
        return questionRepository.findByQuizIdOrderByQuestionOrderAsc(quizId);
    }
    
    // Get specific question
    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }
    
    // Update a question
    public Question updateQuestion(Long questionId, Question questionDetails) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));
        
        // Validate correct answer if changing
        if (questionDetails.getCorrectAnswer() != null) {
            validateCorrectAnswer(questionDetails);
        }
        
        // Update fields if provided
        if (questionDetails.getQuestionText() != null) {
            question.setQuestionText(questionDetails.getQuestionText());
        }
        if (questionDetails.getOptionA() != null) {
            question.setOptionA(questionDetails.getOptionA());
        }
        if (questionDetails.getOptionB() != null) {
            question.setOptionB(questionDetails.getOptionB());
        }
        if (questionDetails.getOptionC() != null) {
            question.setOptionC(questionDetails.getOptionC());
        }
        if (questionDetails.getOptionD() != null) {
            question.setOptionD(questionDetails.getOptionD());
        }
        if (questionDetails.getCorrectAnswer() != null) {
            question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        }
        if (questionDetails.getMarks() != null) {
            question.setMarks(questionDetails.getMarks());
        }
        if (questionDetails.getExplanation() != null) {
            question.setExplanation(questionDetails.getExplanation());
        }
        if (questionDetails.getQuestionOrder() != null) {
            question.setQuestionOrder(questionDetails.getQuestionOrder());
        }
        
        Question updatedQuestion = questionRepository.save(question);
        
        // Update quiz total marks
        updateQuizTotalMarks(question.getQuizId());
        
        return updatedQuestion;
    }
    
    // Delete a question
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));
        
        Long quizId = question.getQuizId();
        questionRepository.deleteById(questionId);
        
        // Update quiz total marks
        updateQuizTotalMarks(quizId);
    }
    
    // Delete all questions for a quiz
    public void deleteAllQuestionsForQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }
        
        questionRepository.deleteByQuizId(quizId);
        updateQuizTotalMarks(quizId);
    }
    
    // Reorder questions in a quiz
    public void reorderQuestions(Long quizId, List<Long> questionIdsInOrder) {
        if (!quizRepository.existsById(quizId)) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }
        
        int order = 1;
        for (Long questionId : questionIdsInOrder) {
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));
            
            if (!question.getQuizId().equals(quizId)) {
                throw new IllegalArgumentException("Question does not belong to the specified quiz");
            }
            
            question.setQuestionOrder(order++);
            questionRepository.save(question);
        }
    }
    
    // Get question count for quiz
    public Long getQuestionCountForQuiz(Long quizId) {
        return questionRepository.countByQuizId(quizId);
    }
    
    // Get total marks for quiz
    public Integer getTotalMarksForQuiz(Long quizId) {
        Integer totalMarks = questionRepository.calculateTotalMarksForQuiz(quizId);
        return totalMarks != null ? totalMarks : 0;
    }
    
    // Validate correct answer format and existence
    private void validateCorrectAnswer(Question question) {
        if (question.getCorrectAnswer() == null) {
            throw new IllegalArgumentException("Correct answer is required");
        }
        
        String correctAnswer = question.getCorrectAnswer().toUpperCase();
        if (!List.of("A", "B", "C", "D").contains(correctAnswer)) {
            throw new IllegalArgumentException("Correct answer must be A, B, C, or D");
        }
        
        // Check if the specified option exists
        String option = question.getOption(correctAnswer);
        if (option == null || option.trim().isEmpty()) {
            throw new IllegalArgumentException("Option " + correctAnswer + " is empty or not provided");
        }
    }
    
    // Update quiz total marks automatically
    private void updateQuizTotalMarks(Long quizId) {
        Integer totalMarks = getTotalMarksForQuiz(quizId);
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + quizId));
        
        quiz.setTotalMarks(totalMarks);
        quizRepository.save(quiz);
    }
    
    // Search questions within a quiz
    public List<Question> searchQuestionsInQuiz(Long quizId, String searchText) {
        if (!quizRepository.existsById(quizId)) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizId);
        }
        
        return questionRepository.searchQuestionsInQuiz(quizId, searchText);
    }
}