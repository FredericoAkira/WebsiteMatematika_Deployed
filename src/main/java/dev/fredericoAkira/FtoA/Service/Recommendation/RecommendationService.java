package dev.fredericoAkira.FtoA.Service.Recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.DTO.DashboardDTO.DailyTaskDTO;
import dev.fredericoAkira.FtoA.DTO.RecommendationDTO.RecommendationDTO;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Question;
import dev.fredericoAkira.FtoA.Entity.QuizScore;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Entity.UserAccessLog;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuestionRepository;
import dev.fredericoAkira.FtoA.Repository.QuizAttemptRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.QuizScoreRepository;
import dev.fredericoAkira.FtoA.Repository.UserAccessRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Service
public class RecommendationService {

    @Autowired
    UserAccessRepository userAccessRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuizAttemptRepository quizAttemptRepository;

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuizScoreRepository quizScoreRepository;

    public List<RecommendationDTO> recommendForUser(String userId) {
        User user = userRepository.findById(new ObjectId(userId))
            .orElseThrow(() -> new RuntimeException("user not found"));
    
        if (user.getGrade() == null || user.getLevel() == null) {
            return Collections.emptyList(); // No recommendation if missing info
        }

        List<String> accessed = userAccessRepository.findByUserId(userId)
            .map(UserAccessLog::getMaterialAccessed)
            .orElse(Collections.emptyList());
    
        String grade = user.getGrade();
        String level = user.getLevel();
    
        // Fetch all materials of same grade
        List<Material> allMaterials = materialRepository.findByGrade(grade).stream()
            .filter(m -> !accessed.contains(m.getMaterialId().toString()))
            .sorted(Comparator.comparingInt(Material::getAccessCount).reversed())
            .collect(Collectors.toList());
    
        // Filter materials based on level-difficulty mapping
        List<Material> recommended = new ArrayList<>();
    
        switch (level) {
            case "Rookie": // Rookie
                recommended = allMaterials.stream()
                    .filter(m -> "Novice".equalsIgnoreCase(m.getDifficulty()))
                    .limit(10)
                    .collect(Collectors.toList());
                break;
    
            case "Explorer": // Explorer
                List<Material> novice = allMaterials.stream()
                    .filter(m -> "Novice".equalsIgnoreCase(m.getDifficulty()))
                    .limit(6)
                    .collect(Collectors.toList());
    
                List<Material> intermediate = allMaterials.stream()
                    .filter(m -> "Intermediate".equalsIgnoreCase(m.getDifficulty()))
                    .limit(4)
                    .collect(Collectors.toList());
    
                recommended.addAll(novice);
                recommended.addAll(intermediate);
                break;
    
            case "Challenger": // Challenger
                List<Material> inter = allMaterials.stream()
                    .filter(m -> "Intermediate".equalsIgnoreCase(m.getDifficulty()))
                    .limit(5)
                    .collect(Collectors.toList());
    
                List<Material> someNovice = allMaterials.stream()
                    .filter(m -> "Novice".equalsIgnoreCase(m.getDifficulty()))
                    .limit(3)
                    .collect(Collectors.toList());
    
                List<Material> littleExpert = allMaterials.stream()
                    .filter(m -> "Expert".equalsIgnoreCase(m.getDifficulty()))
                    .limit(2)
                    .collect(Collectors.toList());
    
                recommended.addAll(inter);
                recommended.addAll(someNovice);
                recommended.addAll(littleExpert);
                break;
    
            case "Master": // Math Master
                List<Material> expert = allMaterials.stream()
                    .filter(m -> "Expert".equalsIgnoreCase(m.getDifficulty()))
                    .limit(7)
                    .collect(Collectors.toList());
    
                List<Material> inter2 = allMaterials.stream()
                    .filter(m -> "Intermediate".equalsIgnoreCase(m.getDifficulty()))
                    .limit(3)
                    .collect(Collectors.toList());
    
                recommended.addAll(expert);
                recommended.addAll(inter2);
                break;
    
            default:
                return Collections.emptyList();
        }
    
        // Make sure final recommendation is at most 10 items
        return recommended.stream()
            .limit(10)
            .map(mat -> new RecommendationDTO(mat.getMaterialName(), mat.getDescription(), mat.getBackgroundImg()))
            .collect(Collectors.toList());
    }
    

    public List<DailyTaskDTO> recommendQuestionsForUser(String userId, String userGrade, String userDifficulty) {
        List<Question> recommended = new ArrayList<>();
        
        // Map custom difficulty to actual quiz difficulty
        String difficulty = switch (userDifficulty) {
            case "Rookie" -> "Novice";
            case "Explorer", "Challenger" -> "Intermediate";
            case "Master" -> "Expert";
            default -> "Novice";
        };
    
        // 1. Get quizIds where user score average < 70%
        Set<String> lowScoreQuizIds = quizScoreRepository.findByUserId(userId).stream()
            .filter(score -> {
                List<String> scores = score.getScores();
                return scores != null && !scores.isEmpty() &&
                    scores.stream().mapToDouble(Double::parseDouble).average().orElse(0.0) < 70.0;
            })
            .map(QuizScore::getQuizId)
            .collect(Collectors.toSet());
    
        // 2. Get materialIds by quizIds & grade
        Set<ObjectId> materialIds = materialRepository.findByQuizzesIn(new ArrayList<>(lowScoreQuizIds)).stream()
            .filter(m -> m.getGrade().equalsIgnoreCase(userGrade))
            .map(Material::getMaterialId)
            .collect(Collectors.toSet());
    
        // 3. Get all questions from those quizzes
        Set<ObjectId> questionIdsFromLowScores = materialRepository.findAllById(materialIds).stream()
            .flatMap(m -> m.getQuizzes().stream())
            .map(ObjectId::new)
            .map(quizRepository::findById)
            .filter(Optional::isPresent)
            .flatMap(optQuiz -> optQuiz.get().getQuizContent().stream())
            .map(ObjectId::new)
            .collect(Collectors.toSet());
    
        List<Question> candidateQuestions = questionRepository.findAllById(questionIdsFromLowScores);
        Collections.shuffle(candidateQuestions);
        recommended.addAll(candidateQuestions.stream().limit(10).toList());
    
        // 4. Fallback by grade & difficulty if needed
        if (recommended.size() < 10) {
            Set<ObjectId> fallbackQuestionIds = quizRepository.findByGradeAndDifficulty(userGrade, difficulty).stream()
                .flatMap(q -> q.getQuizContent().stream())
                .map(ObjectId::new)
                .collect(Collectors.toSet());
    
            List<Question> fallbackQuestions = questionRepository.findAllById(fallbackQuestionIds);
            Collections.shuffle(fallbackQuestions);
    
            for (Question q : fallbackQuestions) {
                if (recommended.size() >= 10) break;
                if (!recommended.contains(q)) recommended.add(q);
            }
        }
    
        // 5. Final fallback: any question
        if (recommended.size() < 10) {
            List<Question> allQuestions = questionRepository.findAll();
            Collections.shuffle(allQuestions);
    
            for (Question q : allQuestions) {
                if (recommended.size() >= 10) break;
                if (!recommended.contains(q)) recommended.add(q);
            }
        }
    
        // Map to DTOs
        return recommended.stream()
            .map(q -> new DailyTaskDTO(
                q.getQuestionId().toString(),
                q.getQuestion(),
                q.getImage(),
                q.getOptions(),
                q.getDifficulty()
            ))
            .collect(Collectors.toList());
    }
}
