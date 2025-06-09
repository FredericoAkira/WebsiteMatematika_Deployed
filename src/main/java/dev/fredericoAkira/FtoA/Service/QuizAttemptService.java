package dev.fredericoAkira.FtoA.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.AnswerDTO.AnswerDTO;
import dev.fredericoAkira.FtoA.DTO.QuizDTO.QuizResultDTO;
import dev.fredericoAkira.FtoA.Entity.Answers;
import dev.fredericoAkira.FtoA.Entity.Question;
import dev.fredericoAkira.FtoA.Entity.QuestionReport;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.QuizAttempt;
import dev.fredericoAkira.FtoA.Entity.QuizScore;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.QuestionReportRepository;
import dev.fredericoAkira.FtoA.Repository.QuestionRepository;
import dev.fredericoAkira.FtoA.Repository.QuizAttemptRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.QuizScoreRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Service
public class QuizAttemptService {
    @Autowired
    private QuizAttemptRepository accessedQuizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionReportRepository questionReportRepository;

    @Autowired
    private QuizScoreRepository quizScoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    public ApiResponse<?> addAccess(String userId, String quizId){
        // Check if a QuizAttempt already exists for the given userId and quizId
        Optional<QuizAttempt> existingAccess = accessedQuizRepository.findByUserIdAndQuizId(userId, quizId);
    
        if (existingAccess.isPresent()) {
            // If it exists, do nothing and return a message indicating it's already there
                return ApiResponse.success(existingAccess.get().getAttemptId().toString());
        }

        QuizAttempt access = new QuizAttempt();
        access.setUserId(userId);
        access.setQuizId(quizId);

        QuizAttempt saved = accessedQuizRepository.save(access);

        return ApiResponse.success(saved.getAttemptId().toString());
    }

    public ApiResponse<?> storeQuestions(String attemptId, List<String> questionIds) {
        QuizAttempt access = accessedQuizRepository.findById(new ObjectId(attemptId))
            .orElseThrow(() -> new RuntimeException("New Quiz"));

        access.setQuestionIds(questionIds);
        accessedQuizRepository.save(access);

        return ApiResponse.success("question recorded");
    }

    private int parseGrade(String grade) {
        try {
            return Integer.parseInt(grade);
        } catch (NumberFormatException e) {
            return -1; // invalid index
        }
    }

    public ApiResponse<?> completeAttemptAndCalculateResult(String attemptId) {
        System.out.println(attemptId);
        QuizAttempt attempt = quizAttemptRepository.findById(new ObjectId(attemptId)).orElseThrow(() -> new RuntimeException("attempt not found"));
        int totalQuestions = attempt.getQuestionIds().size();
        long correctAnswers = attempt.getAnsweredQuestion().stream()
            .filter(answer -> answer.getIsCorrect())
            .count();
    
        double percentage = (double) correctAnswers / totalQuestions * 100;
    
        // Calculate point based on difficulty
        Quiz quiz = quizRepository.findById(new ObjectId(attempt.getQuizId()))
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        int points = switch (quiz.getDifficulty().toLowerCase()) {
            case "Novice" -> 50;
            case "Intermediate" -> 80;
            case "Expert" -> 100;
            default -> 50;
        };

        // Get or create QuizResult
        QuizScore result = quizScoreRepository
            .findByUserIdAndQuizId(attempt.getUserId(), attempt.getQuizId())
            .orElseGet(() -> {
                QuizScore newResult = new QuizScore();
                newResult.setUserId(attempt.getUserId());
                newResult.setQuizId(attempt.getQuizId());
                newResult.setScores(new ArrayList<>());
                return newResult;
            });
    
        boolean isFirstAttempt = result.getScores().isEmpty();
        result.getScores().add(String.format("%.2f", percentage));
        quizScoreRepository.save(result);
        
        // Update user EXP only on first attempt
        int awardedPoints = 0;
        int updatedExp = 0;
        User user = userRepository.findById(new ObjectId(attempt.getUserId()))
            .orElseThrow(() -> new RuntimeException("User not found"));

        final int index = parseGrade(user.getGrade());

        int point = Optional.ofNullable(user.getPoint())
            .filter(exp -> index >= 0 && index < exp.size())
            .map(exp -> exp.get(index))
            .orElse(0);
        
        List<Integer> userPoints = user.getPoint();
        if (userPoints == null) {
            userPoints = new ArrayList<>();
        }
        
        // Ensure list is large enough
        while (userPoints.size() <= index) {
            userPoints.add(0);
        }

        if (isFirstAttempt) {
            awardedPoints = (int) percentage * points / 100;
            updatedExp = point + awardedPoints;
            userPoints.set(index, updatedExp);
            user.setPoint(userPoints);  // set the full updated list back to user
            userRepository.save(user);
        } else {
            updatedExp = point; // EXP unchanged
        }
    
        // Delete attempt
        accessedQuizRepository.deleteById(attempt.getAttemptId());
        QuizResultDTO resultDTO = new QuizResultDTO(
            awardedPoints,       // pointsEarned
            percentage,          // correctPercentage
            updatedExp           // updatedExp
        );

        return ApiResponse.success(resultDTO);
    }

    public ApiResponse<?> editAccess(String attemptId, String questionId, String userAnswer) {
        QuizAttempt access = accessedQuizRepository.findById(new ObjectId(attemptId))
            .orElseThrow(() -> new RuntimeException("New Quiz"));
        Question question = questionRepository.findById(new ObjectId(questionId))
            .orElseThrow(() -> new RuntimeException("Question not Found"));
    
        if (access.getAnsweredQuestion() == null) {
            access.setAnsweredQuestion(new ArrayList<>());
        }
    
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(userAnswer);
    
        access.getAnsweredQuestion().add(new Answers(
            questionId,
            userAnswer,
            isCorrect,
            question.getCorrectAnswer()
        ));
    
        accessedQuizRepository.save(access);
    
        // Update QuestionReport
        QuestionReport report = questionReportRepository
            .findByUserIdAndQuizIdAndQuestionId(access.getUserId(), access.getQuizId(), questionId)
            .orElseGet(() -> {
                QuestionReport newReport = new QuestionReport();
                newReport.setUserId(access.getUserId());
                newReport.setQuizId(access.getQuizId());
                newReport.setQuestionId(questionId);
                newReport.setCorrectCount(0);
                newReport.setFalseCount(0);
                return newReport;
            });
    
        if (isCorrect) {
            report.setCorrectCount(report.getCorrectCount() + 1);
        } else {
            report.setFalseCount(report.getFalseCount() + 1);
        }
    
        questionReportRepository.save(report);

        return ApiResponse.success(new AnswerDTO(userAnswer, isCorrect, question.getCorrectAnswer()));
    }
    

    public ApiResponse<?> getAccessDetail(String userId, String quizName){
        String quizId;

        if ("daily".equalsIgnoreCase(quizName)) {
            quizId = "daily"; // special case, use quizName directly
        } else {
            Optional<Quiz> quizOpt = quizRepository.findByQuizName(quizName);
            if (quizOpt.isEmpty()) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST, "Quiz not found");
            }
            quizId = quizOpt.get().getQuizId().toString();
        }

        Optional<QuizAttempt> existingAccess = accessedQuizRepository.findByUserIdAndQuizId(userId, quizId);
        if (existingAccess.isPresent()) {
            return ApiResponse.success(existingAccess.get().getAnsweredQuestion());
        }
        return ApiResponse.success("new Attempt");
    }
}
