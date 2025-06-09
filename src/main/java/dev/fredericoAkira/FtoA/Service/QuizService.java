package dev.fredericoAkira.FtoA.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.QuizListDTO;
import dev.fredericoAkira.FtoA.DTO.QuizDTO.QuizContentDTO;
import dev.fredericoAkira.FtoA.DTO.QuizDTO.QuizDetailDTO;
import dev.fredericoAkira.FtoA.DTO.QuizDTO.QuizListAdminDTO;
import dev.fredericoAkira.FtoA.DTO.QuizDTO.UserQuestionDTO;
import dev.fredericoAkira.FtoA.Entity.Answers;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Question;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.QuizAttempt;
import dev.fredericoAkira.FtoA.Entity.QuizScore;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuestionRepository;
import dev.fredericoAkira.FtoA.Repository.QuizAttemptRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.QuizScoreRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Util.SortingUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {
    @Autowired
    QuizRepository quizRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    QuizAttemptRepository quizAttemptRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuizScoreRepository quizScoreRepository;

    public ApiResponse<?> addQuiz(Quiz request, String userId) {
        Quiz quiz = new Quiz();
        quiz.setQuizName(request.getQuizName());
        quiz.setGrade(request.getGrade());
        quiz.setDifficulty(request.getDifficulty());

        List<String> questionIds = new ArrayList<>();
        for (Question q : request.getQuestions()) {
            String questionId = questionService.upsertQuestion(q);
            questionIds.add(questionId);
        }

        quiz.setQuizContent(questionIds);
        
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        user.setLastQuiz(quiz.getQuizName());

        userRepository.save(user);

        quizRepository.save(quiz);

        return ApiResponse.success("Quiz created successfully");
    }

    public ApiResponse<?> editQuiz(Quiz request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setQuizName(request.getQuizName());
        quiz.setGrade(request.getGrade());
        quiz.setDifficulty(request.getDifficulty());

        List<String> questionIds = new ArrayList<>();
        for (Question q : request.getQuestions()) {
            String questionId = questionService.upsertQuestion(q);
            questionIds.add(questionId);
        }

        quiz.setQuizContent(questionIds);
        quizRepository.save(quiz);

        return ApiResponse.success("Quiz and questions updated successfully");
    }

    public ApiResponse<?> deleteQuiz(String quizId) {
        if (!quizRepository.existsById(new ObjectId(quizId))) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Quiz not found");
        }

        // Remove the quizId from all materials that reference it
        List<Material> materials = materialRepository.findAll();
        for (Material material : materials) {
            if (material.getQuizzes() != null && material.getQuizzes().removeIf(id -> id.equals(quizId))) {
                materialRepository.save(material);
            }
        }

        quizRepository.deleteById(new ObjectId(quizId));
        return ApiResponse.success("Quiz deleted and removed from all materials");
    }

    public ApiResponse<Page<QuizListAdminDTO>>getAdminQuizList(
        String userId,
        String materialName,
        String filterDifficulty,
        String filterGrade,
        String searchQuery,
        Pageable pageable
    ){
        // untuk mengecek level pengguna dan quiz terakhir yang diakses
        // akan digunakan untuk urutan tampilan quiz
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, List<String>> levelSortOrder = Map.of(
            "Rookie", List.of("Novice", "Intermediate", "Expert"),
            "Explorer", List.of("Novice", "Intermediate", "Expert"),
            "Challenger", List.of("Intermediate", "Novice", "Expert"),
            "Master", List.of("Expert", "Intermediate", "Novice")
        );

        List<Quiz> quizzes = quizRepository.findAll();
        List<Quiz> sortedQuiz = SortingUtil.sortQuizzes(quizzes, user.getLastQuiz(), user.getLevel(), levelSortOrder);

        sortedQuiz = sortedQuiz.stream()
            .filter(quiz -> (filterDifficulty == null || filterDifficulty.isBlank() || quiz.getDifficulty().equalsIgnoreCase(filterDifficulty)))
            .filter(quiz -> (filterGrade == null || filterGrade.isBlank() || quiz.getGrade().equalsIgnoreCase(filterGrade)))
            .filter(quiz -> (searchQuery == null || searchQuery.isEmpty() || quiz.getQuizName().toLowerCase().contains(searchQuery.toLowerCase())))
            .filter(quiz -> {
                if (materialName == null || materialName.isEmpty()) return true;
                List<Material> materials = materialRepository.findByQuizzesContaining(quiz.getQuizId().toString());
                return materials.stream().anyMatch(m -> m.getMaterialName().equalsIgnoreCase(materialName));
            })
            .collect(Collectors.toList());

        List<QuizListAdminDTO> quizDTO = sortedQuiz.stream()
            .map(quiz -> {
                String totalContent = String.valueOf(quiz.getQuizContent() != null ? quiz.getQuizContent().size() : 0);
                List<Material> materials = materialRepository.findByQuizzesContaining(quiz.getQuizId().toString());
                String[] materialNames = materials.stream()
                    .map(Material::getMaterialName)
                    .toArray(String[]::new);
                
                return new QuizListAdminDTO(
                    quiz.getQuizId().toString(),
                    materialNames,
                    quiz.getQuizName(),
                    quiz.getDifficulty(),
                    quiz.getGrade(),
                    totalContent
            );
        }).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), quizDTO.size());
        List<QuizListAdminDTO> pagedQuizzes = quizDTO.subList(start, end);

        return ApiResponse.success(new PageImpl<>(pagedQuizzes, pageable, quizDTO.size()));
    }
    
    public ApiResponse<QuizDetailDTO>getAdminQuizDetail(String quizName){
        Quiz quiz = quizRepository.findByQuizName(quizName).orElseThrow(() -> new RuntimeException("Quiz not Found"));
        List<Question> questions = quiz.getQuizContent() == null || quiz.getQuizContent().isEmpty()
            ? Collections.emptyList()
            : questionRepository.findAllById(
                quiz.getQuizContent().stream()
                    .map(ObjectId::new)
                    .collect(Collectors.toList())
            );
        
        List<QuizContentDTO> quizContentList = questions.stream()
            .map(question -> new QuizContentDTO(
                question.getQuestionId().toString(),
                question.getQuestion(),
                question.getImage(),
                question.getOptions(),
                question.getCorrectAnswer(),
                question.getDifficulty(),
                question.getSolution()
            ))
            .collect(Collectors.toList());

        QuizDetailDTO body = new QuizDetailDTO(
            quiz.getQuizId().toString(),
            quiz.getQuizName(),
            quizContentList,
            quiz.getGrade(),
            quiz.getDifficulty()
        );
        return ApiResponse.success(body);
    }

    public ApiResponse<Page<QuizListDTO>>getQuizList(
        String userId,
        String materialName,
        String filterDifficulty,
        String filterGrade,
        String searchQuery,
        Pageable pageable
    ){
        // untuk mengecek level pengguna dan quiz terakhir yang diakses
        // akan digunakan untuk urutan tampilan quiz
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, List<String>> levelSortOrder = Map.of(
            "Rookie", List.of("Novice", "Intermediate", "Expert"),
            "Explorer", List.of("Novice", "Intermediate", "Expert"),
            "Challenger", List.of("Intermediate", "Novice", "Expert"),
            "Master", List.of("Expert", "Intermediate", "Novice")
        );

        List<Quiz> quizzes = quizRepository.findAll();
        List<Quiz> sortedQuiz = SortingUtil.sortQuizzes(quizzes, user.getLastQuiz(), user.getLevel(), levelSortOrder);

        sortedQuiz = sortedQuiz.stream()
            .filter(quiz -> (filterDifficulty == null || filterDifficulty.isBlank() || quiz.getDifficulty().equalsIgnoreCase(filterDifficulty)))
            .filter(quiz -> (filterGrade == null || filterGrade.isBlank() || quiz.getGrade().equalsIgnoreCase(filterGrade)))
            .filter(quiz -> (searchQuery == null || searchQuery.isEmpty() || quiz.getQuizName().toLowerCase().contains(searchQuery.toLowerCase())))
            .filter(quiz -> {
                if (materialName == null || materialName.isEmpty()) return true;
                List<Material> materials = materialRepository.findByQuizzesContaining(quiz.getQuizId().toString());
                return materials.stream().anyMatch(m -> m.getMaterialName().equalsIgnoreCase(materialName));
            })
            .collect(Collectors.toList());

        List<QuizListDTO> quizDTO = sortedQuiz.stream()
        .map(quiz -> {
            String quizIdStr = quiz.getQuizId().toString();
            Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findByUserIdAndQuizId(userId, quizIdStr);
            Optional<Quiz> quizOpt = quizRepository.findById(quiz.getQuizId());
            Optional<QuizScore> quizScore = quizScoreRepository.findByUserIdAndQuizId(userId, quizIdStr);

            double progress = 0.0;
            int score = 0;
            int avgScore = 0;
            List<Answers> answered = new ArrayList<>();
            List<String> scores = quizScore.map(QuizScore::getScores).orElse(Collections.emptyList());

            if (attemptOpt.isPresent() && quizOpt.isPresent()) {
                QuizAttempt attempt = attemptOpt.get();

                answered = Optional.ofNullable(attempt.getAnsweredQuestion()).orElse(new ArrayList<>());
                int totalQuestions = Optional.ofNullable(attempt.getQuestionIds()).map(List::size).orElse(0);

                if (totalQuestions > 0) {
                    double doubleProgress = (double) answered.size() * 100 / totalQuestions;
                    progress = Math.round(doubleProgress);
                }

                long correctCount = answered.stream()
                    .filter(ans -> Boolean.TRUE.equals(ans.getIsCorrect()))
                    .count();

                if (!answered.isEmpty()) {
                    score = (int) correctCount * 100 / totalQuestions; // score as a percentage
                }
            } else {
                if (scores != null && !scores.isEmpty()) {
                    double doubleScore = Double.parseDouble(scores.get(scores.size() - 1));
                    score = (int) doubleScore;

                    double average = scores.stream()
                        .mapToDouble(Double::parseDouble)
                        .average()
                        .orElse(0.0);
                    avgScore = (int) average;
                }
            }

            return new QuizListDTO(
                quizIdStr,
                quiz.getQuizName(),
                quiz.getDifficulty(),
                quiz.getGrade(),
                progress,
                score,
                avgScore
            );
        })
        .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), quizDTO.size());
        List<QuizListDTO> pagedQuizzes = quizDTO.subList(start, end);

        return ApiResponse.success(new PageImpl<>(pagedQuizzes, pageable, quizDTO.size()));
    }

    public ApiResponse<List<UserQuestionDTO>>getQuestions(String attemptId, String quizName){
        Quiz quiz = quizRepository.findByQuizName(quizName).orElseThrow(() -> new RuntimeException("Quiz not found"));
        QuizAttempt attempt = quizAttemptRepository.findById(new ObjectId(attemptId))
        .orElseThrow(() -> new RuntimeException("Attempt not found"));

        List<String> questionIdString;

        if (attempt.getQuestionIds() != null && !attempt.getQuestionIds().isEmpty()) {
            questionIdString = attempt.getQuestionIds();
        } else {
            // 🎲 Randomly select 3 questions from the quiz
            List<String> quizContent = quiz.getQuizContent();
            Collections.shuffle(quizContent);
            questionIdString = quizContent.stream().limit(5).collect(Collectors.toList());
    
            // 💾 Save selected question IDs to the attempt
            attempt.setQuestionIds(questionIdString);
            quizAttemptRepository.save(attempt);
        }

        List<ObjectId> questionObjectIds = questionIdString.stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());

        // 🧾 Fetch questions
        List<Question> questions = questionRepository.findAllById(questionObjectIds);

        List<UserQuestionDTO> userQuestions = questions.stream()
            .map(question -> new UserQuestionDTO(
                question.getQuestionId().toString(),
                question.getQuestion(),
                question.getImage(),
                question.getOptions()
            )).toList();

        return ApiResponse.success(userQuestions);
    }

    public ApiResponse<?> getSolution (String questionId){
        Question question = questionRepository.findById(new ObjectId(questionId)).orElseThrow(() -> new RuntimeException("Question not found"));
        return ApiResponse.success(question.getSolution());
    }
}
