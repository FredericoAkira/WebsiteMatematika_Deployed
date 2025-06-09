package dev.fredericoAkira.FtoA.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LevelUpDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDashboardDTO;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.UserDetailDTO;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.User.RecentMaterialDTO;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.User.RecentQuizDTO;
import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.StudentListDTO;
import dev.fredericoAkira.FtoA.Entity.Answers;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.QuizAttempt;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.DailyRecommendationRepository;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuizAttemptRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.UserAccessRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Service.AccessLog.AccessLogService;
import dev.fredericoAkira.FtoA.Util.PropertyCopyUtil;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository accessedQuizRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private UserAccessRepository userAccessRepository;

    @Autowired
    private DailyRecommendationRepository dailyRecommendationRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }

    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight
    @Transactional
    public void resetDoDaily() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        for (User user : users) {
            if (!yesterday.equals(user.getLastDoDailyDate())) {
                user.setDailyStreak(0); // Break streak
            }
            user.setDoDaily(false); // Reset flag
        }
        
        dailyRecommendationRepository.deleteByDateBefore(LocalDate.now());
        quizAttemptRepository.deleteAllByQuizId("daily");

        userRepository.saveAll(users);
    }
    
    private int parseGrade(String grade) {
        try {
            return Integer.parseInt(grade);
        } catch (NumberFormatException e) {
            return -1; // invalid index
        }
    }
    

    public ApiResponse<UserDetailDTO> getUserProfile (String userId){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        final int index = parseGrade(user.getGrade());

        int point = Optional.ofNullable(user.getPoint())
            .filter(points -> index >= 0 && index < points.size())
            .map(points -> points.get(index))
            .orElse(0);

        UserDetailDTO userProfile = new UserDetailDTO(
            user.getUsername(),
            user.getProfilePhoto(),
            user.getEmail(),
            user.getLevel(),
            point,
            user.getGrade(),
            user.getDailyStreak()
        );

        return ApiResponse.success(userProfile);
    }

    public ApiResponse<StudentDashboardDTO> getDashboard(String userId, String quizParam, String materialParam) throws UsernameNotFoundException {
        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        // last quiz
        RecentQuizDTO latestQuiz = null;
        if (user.getLastQuiz() != null && !user.getLastQuiz().isEmpty()) {
            // log.info("Fetching last quiz: " + user.getLastQuiz());
            Optional<Quiz> quizOptional = quizRepository.findByQuizName(user.getLastQuiz());
            Optional<Material> materialOptional = materialRepository.findByQuizzesContains(quizOptional.get().getQuizId().toString());

            
            latestQuiz = quizOptional.map(quiz -> {
                Optional<QuizAttempt> accessedQuizOptional = accessedQuizRepository.findByquizId(quiz.getQuizId().toString());
                int answeredQuestions = accessedQuizOptional
                    .map(aq -> {
                        List<Answers> answers = aq.getAnsweredQuestion();
                        return answers != null ? answers.size() : 0;
                    })
                    .orElse(0);
                int totalQuestions = quiz.getQuizContent().size();
                return new RecentQuizDTO(
                    quiz.getQuizName(),
                    quiz.getGrade(),
                    totalQuestions > 0 ? (double) answeredQuestions / totalQuestions : 0,
                    quiz.getDifficulty(),
                    materialOptional.map(Material::getMaterialName).orElse("")
                );
            }).orElse(null);
        }

        // last material
        RecentMaterialDTO lastMaterial = null;
        if (user.getLastMaterial() != null && !user.getLastMaterial().isEmpty()) {
            // log.info("Fetching last material: " + user.getLastMaterial());
            Optional<Material> optionalMaterial = materialRepository.findByMaterialName(user.getLastMaterial());
            lastMaterial = optionalMaterial.map(material -> {
                return new RecentMaterialDTO(
                    material.getMaterialName(),
                    material.getDifficulty(),
                    material.getGrade()
                );
            }).orElse(null);
        }

        // quiz chart
        List<DataDTO> quizGroupStats = userAccessRepository.findByUserId(userId)
            .map(log -> {
                List<String> quizIds = Optional.ofNullable(log.getQuizAccessed()).orElse(Collections.emptyList());

                List<Quiz> quizzes = quizRepository.findAllById(
                    quizIds.stream().map(ObjectId::new).collect(Collectors.toList())
                );

                Map<String, Long> grouped = quizzes.stream().collect(Collectors.groupingBy(quiz -> {
                    switch (quizParam.toLowerCase()) {
                        case "difficulty":
                            return quiz.getDifficulty();
                        case "grade":
                            return quiz.getGrade();
                        case "name":
                            return quiz.getQuizName();
                        default:
                            return "Unknown";
                    }
                }, Collectors.counting()));

                List<DataDTO> dtos = grouped.entrySet().stream()
                    .map(entry -> new DataDTO(entry.getValue().toString(), entry.getKey()))
                    .collect(Collectors.toList());

                return dtos;
            })
            .orElse(Collections.emptyList());

        // Material chart
        List<DataDTO> materialGroupStats = userAccessRepository.findByUserId(userId)
            .map(log -> {
                List<String> materialIds = Optional.ofNullable(log.getMaterialAccessed()).orElse(Collections.emptyList());

                List<Material> materials = materialRepository.findAllById(
                    materialIds.stream().map(ObjectId::new).collect(Collectors.toList())
                );

                Map<String, Long> grouped = materials.stream().collect(Collectors.groupingBy(material -> {
                    switch (materialParam.toLowerCase()) {
                        case "difficulty":
                            return material.getDifficulty();
                        case "grade":
                            return material.getGrade();
                        case "name":
                            return material.getMaterialName();
                        default:
                            return "Unknown";
                    }
                }, Collectors.counting()));

                List<DataDTO> dtos = grouped.entrySet().stream()
                    .map(entry -> new DataDTO(entry.getValue().toString(), entry.getKey()))
                    .collect(Collectors.toList());

                return dtos;
            })
            .orElse(Collections.emptyList());

        // ✅ Handle null student list
        List<StudentListDTO> studentList = user.getStudents() != null
            ? user.getStudents().stream()
                // .filter(Objects::nonNull)
                .map(ObjectId::new)
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(users -> new StudentListDTO(
                    users.getUsername(),
                    users.getGrade() != null ? users.getGrade() : null
                ))
                .collect(Collectors.toList())
            : Collections.emptyList();

        // ✅ Construct final DTO
        StudentDashboardDTO userResponse = new StudentDashboardDTO(
            quizGroupStats,
            materialGroupStats,
            lastMaterial, // Will be null if no last material
            latestQuiz,   // Will be null if no last quiz
            studentList,
            user.getDoDaily()
        );

        return ApiResponse.success(userResponse);
    }

    public ApiResponse<?> editUser(User request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not Found"));
        PropertyCopyUtil.copyNonNullProperties(request, user);

        userRepository.save(user);

        return ApiResponse.success("User Data Updated Successfully");
    }

    public ApiResponse<?> gradeUp(String userId){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        Integer userGrade = user.getGrade() != null ? Integer.parseInt(user.getGrade()) : 0;

        user.setGrade(String.valueOf(userGrade + 1));
        userRepository.save(user);

        return ApiResponse.success("Sukses Naik Kelas");
    }

    public ApiResponse<?> getDetailbyId(String userId){
        User userDetail = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("user not found"));
        return ApiResponse.success(userDetail);
    }

    public ApiResponse<?> setLatestAccess(String type, String userId, String itemName){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        String materialId = materialRepository.findByMaterialName(itemName)
            .map(material -> material.getMaterialId().toString())
            .orElse("");
        String quizId = quizRepository.findByQuizName(itemName)
            .map(quiz -> quiz.getQuizId().toString())
            .orElse("");
        
        switch (type) {
            case "material":
                user.setLastMaterial(itemName);
                if(!user.getRole().toString().equalsIgnoreCase("admin")){
                    accessLogService.addOrEditLog(userId, materialId, "");
                }
                break;
            case "quiz":
                user.setLastQuiz(itemName);
                if(!user.getRole().toString().equalsIgnoreCase("admin")){
                    accessLogService.addOrEditLog(userId, "", quizId);
                }
                break;
            case "topic":
                user.setLastTopic(itemName);
                break;
            case "deleteMaterial":
                if(user.getLastMaterial().equalsIgnoreCase(itemName)){
                    user.setLastMaterial("");
                }
            case "deleteQuiz":
                if(user.getLastQuiz().equalsIgnoreCase(itemName)){
                    user.setLastQuiz("");
                }
            case "deleteTopic":
                if(user.getLastTopic().equalsIgnoreCase(itemName)){
                    user.setLastTopic("");
                }
            default:
                break;
        }
        userRepository.save(user);
        return ApiResponse.success("Last access updated");
    }

    public ApiResponse<?> completeDoDaily(String userId) {
        // System.out.println("USERID SENT" + userId);
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("user not found"));

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (today.equals(user.getLastDoDailyDate())) {
            // Already done today, no changes
            return ApiResponse.success("already done today");
        }

        if (yesterday.equals(user.getLastDoDailyDate())) {
            user.setDailyStreak(user.getDailyStreak() + 1); // Continue streak
        } else {
            user.setDailyStreak(1); // Reset streak
        }

        user.setDoDaily(true);
        user.setLastDoDailyDate(today);
        userRepository.save(user);
        return ApiResponse.success("Daily data updated successfully");
    }

    public ApiResponse<?> getLevelCapByGrade(String gradeParam) {
        Map<String, Integer> difficultyWeights = Map.of(
            "novice", 50,
            "intermediate", 80,
            "expert", 100
        );

        List<Quiz> quizzes = quizRepository.findAll();
    
        // Filter quizzes by the given grade
        List<Quiz> filteredQuizzes = quizzes.stream()
            .filter(q -> q.getGrade().equalsIgnoreCase(gradeParam))
            .collect(Collectors.toList());
    
        // Group filtered quizzes by difficulty and count
        Map<String, Long> countByDifficulty = filteredQuizzes.stream()
            .collect(Collectors.groupingBy(Quiz::getDifficulty, Collectors.counting()));
    
        // Calculate total weighted score for this grade
        int totalWeight = 0;
        for (Map.Entry<String, Long> entry : countByDifficulty.entrySet()) {
            String difficulty = entry.getKey();
            long count = entry.getValue();
    
            int weight = difficultyWeights.getOrDefault(difficulty.toLowerCase(), 0);
            totalWeight += weight * count;
        }
    
        return ApiResponse.success(totalWeight);
    }

    public ApiResponse<?> levelUp(String userId, int totalProgress, int levelCap, String currentLevel){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        String message = "";

        int percentage = (int) ((double) totalProgress / levelCap * 100);

        String newLevel;
        if (percentage <= 40) newLevel = "Rookie";
        else if (percentage <= 60) newLevel = "Challenger";
        else if (percentage <= 80) newLevel = "Explorer";
        else newLevel = "Master";

        boolean levelChanged = !newLevel.equals(currentLevel);

        if(levelChanged){
            user.setLevel(newLevel);
            userRepository.save(user);
            switch (newLevel) {
                case "Challenger":
                    message = "⚔️ Ayo selesaikan soal - soal yang tersisa dan raih level Master!";
                    break;
                case "Explorer":
                    message = "🧭 Hebat! Sekarang kamu sudah siap untuk mengerjakan soal - soal yang lebih sulit!";
                    break;
                case "Master":
                    message = "👑 Kamu mencapai level tertinggi! Ayo lanjutkan petualanganmu ke kelas berikutnya!";
                    break;
                case "Rookie":
                    message = "🎉 Selamat Datang! Ayo jelajahi materi dan kuis yang ada pada kelas ini!";
                    break;
                default:
                    message = "Level up! Perjalanan Matematikamu terus berlanjut — Jangan Menyerah!";
            }
        }

        LevelUpDTO levelUpDTO = new LevelUpDTO(
            newLevel,
            message,
            levelChanged
        );

        return ApiResponse.success(levelUpDTO);
    }

}