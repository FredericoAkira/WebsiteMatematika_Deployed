package dev.fredericoAkira.FtoA.Controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.AnswerDTO.AnswerSubmitDTO;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.DoubleStringReq;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.QuizReqDTO;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Service.QuizAttemptService;
import dev.fredericoAkira.FtoA.Service.QuizService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;
    // private final QuestionService questionService;

    @PostMapping("/admin/addQuiz")
    public ResponseEntity<ApiResponse<?>> addQuiz(@RequestBody QuizReqDTO quiz) {
        return ResponseEntity.ok(quizService.addQuiz(quiz.getQuiz(), quiz.getUserId()));
    }

    @GetMapping("/admin/getQuiz")
    public ResponseEntity<?> getAllNames(
        Pageable pageable,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String materialName,
        @RequestParam(required = false) String filterDifficulty,
        @RequestParam(required = false) String filterGrade,
        @RequestParam(required = false) String searchQuery
    ) {
        return ResponseEntity.ok(quizService.getAdminQuizList(userId, materialName, filterDifficulty, filterGrade, searchQuery, pageable));
    }

    @GetMapping("/admin/getQuizDetail")
    public ResponseEntity<?> getQuizDetail(String quizName) {
        return ResponseEntity.ok(quizService.getAdminQuizDetail(quizName));
    }

    @PutMapping("/admin/editQuiz")
    public ResponseEntity<ApiResponse<?>> editQuiz(@RequestBody Quiz quiz) {
        return ResponseEntity.ok(quizService.editQuiz(quiz));
    }

    @DeleteMapping("/admin/deleteQuiz/{quizId}")
    public ResponseEntity<ApiResponse<?>> deleteQuiz(@PathVariable String quizId) {
        return ResponseEntity.ok(quizService.deleteQuiz(quizId));
    }

    @GetMapping("/getQuizList")
    public ResponseEntity<ApiResponse<?>> getQuizListUser (
        Pageable pageable,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String materialName,
        @RequestParam(required = false) String filterDifficulty,
        @RequestParam(required = false) String filterGrade,
        @RequestParam(required = false) String searchQuery
    ) {
        return ResponseEntity.ok(quizService.getQuizList(userId, materialName, filterDifficulty, filterGrade, searchQuery, pageable));
    }

    @GetMapping("/getQuestions")
    public ResponseEntity<ApiResponse<?>> getQuestions (
        @RequestParam String quizName,
        @RequestParam String attemptId
    ) {
        return ResponseEntity.ok(quizService.getQuestions(attemptId, quizName));
    }

    @PostMapping("/accessQuiz")
    public ResponseEntity<ApiResponse<?>> accessQuiz(@RequestBody DoubleStringReq request) {
        return ResponseEntity.ok(quizAttemptService.addAccess(request.getItemOne(), request.getItemTwo()));
    }

    @PostMapping("/answerQuiz")
    public ResponseEntity<ApiResponse<?>> answerQuiz(@RequestBody AnswerSubmitDTO request) {
        return ResponseEntity.ok(quizAttemptService.editAccess(request.getAttemptId(), request.getQuestionId(), request.getUserAnswer()));
    }

    @GetMapping("/resultQuiz")
    public ResponseEntity<ApiResponse<?>> getQuizResult (
        @RequestParam String userId,
        @RequestParam String quizName
    ) {
        return ResponseEntity.ok(quizAttemptService.getAccessDetail(userId, quizName));
    }

    @PostMapping("/calculateResult")
    public ResponseEntity<ApiResponse<?>> getFinalResult(@RequestParam String attemptId) {
        return ResponseEntity.ok(quizAttemptService.completeAttemptAndCalculateResult(attemptId));
    }

    @GetMapping("/getSolution")
    public ResponseEntity<ApiResponse<?>> getSolution(@RequestParam String questionId) {
        return ResponseEntity.ok(quizService.getSolution(questionId));
    }
    
    
    
    // @DeleteMapping("/deleteQuestion/{questionId}")
    // public ResponseEntity<ApiResponse<?>> deleteQuestion(@PathVariable String questionId) {
    //     return ResponseEntity.ok(questionService.deleteQuestion(questionId));
    // }
}

