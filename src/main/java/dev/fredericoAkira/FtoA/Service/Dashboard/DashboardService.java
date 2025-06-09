package dev.fredericoAkira.FtoA.Service.Dashboard;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.AdminDashboardDTO;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.UserDTO;
import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Service
public class DashboardService {

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    UserRepository userRepository;

    private String getFilterValueMaterial(Material material, String filter) {
        if (filter == null) return "Unknown";
        return switch (filter.toLowerCase()) {
            case "grade" -> Optional.ofNullable(material.getGrade()).orElse("Unknown");
            case "difficulty" -> Optional.ofNullable(material.getDifficulty()).orElse("Unknown");
            default -> "Unknown";
        };
    }

    public List<DataDTO> countTopicsByFilter(List<Material> materials, String topicFilter) {
        if (topicFilter == null) {
            return materials.stream()
                .flatMap(m -> m.getTopics().stream())
                .filter(Objects::nonNull)
                .filter(topic -> topic != null)
                .map(topic -> new DataDTO("1", topic)) // or however you're using this
                .collect(Collectors.toList());
        }
        switch (topicFilter.toLowerCase()) {
            case "grade":
                return materials.stream()
                    .collect(Collectors.groupingBy(
                        Material::getGrade,
                        Collectors.summingLong(m -> m.getTopics().size())
                    ))
                    .entrySet().stream()
                    .map(entry -> new DataDTO(String.valueOf(entry.getValue()), entry.getKey()))
                    .collect(Collectors.toList());

            case "difficulty":
                return materials.stream()
                    .collect(Collectors.groupingBy(
                        Material::getDifficulty,
                        Collectors.summingLong(m -> m.getTopics().size())
                    ))
                    .entrySet().stream()
                    .map(entry -> new DataDTO(String.valueOf(entry.getValue()), entry.getKey()))
                    .collect(Collectors.toList());

            case "material":
                return materials.stream()
                    .map(m -> new DataDTO(String.valueOf(m.getTopics().size()), m.getMaterialName()))
                    .collect(Collectors.toList());

            default:
                return List.of(); // Or throw exception if invalid filter
        }
    }

    private String getFilterValueQuiz(Quiz quiz, String filter) {
        if (filter == null) return "Unknown";
        return switch (filter.toLowerCase()) {
            case "grade" -> Optional.ofNullable(quiz.getGrade()).orElse("Unknown");
            case "difficulty" -> Optional.ofNullable(quiz.getDifficulty()).orElse("Unknown");
            default -> "Unknown";
        };
    }

    private String getFilterValueStudent(User user, String filter) {
        if (filter == null) return "Unknown";
        return switch (filter.toLowerCase()) {
            case "grade" -> Optional.ofNullable(user.getGrade()).orElse("Unknown");
            case "level" -> Optional.ofNullable(user.getLevel()).orElse("Unknown");
            default -> filter;
        };
    }

    
    public ApiResponse<AdminDashboardDTO> adminDashboard(
        String userId,
        String filterMaterial,
        String filterTopic,
        String filterQuiz,
        String filterStudent
    ){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        
        // Material Chart
        List<Material> materials = materialRepository.findAll();
        Map<String, Long> groupedCount = materials.stream()
            .collect(Collectors.groupingBy(
                material -> getFilterValueMaterial(material, filterMaterial), // Group by grade or difficulty
                Collectors.counting() // Count the occurrences
            ));

        List<DataDTO> materialChart = groupedCount.entrySet().stream()
            .map(entry -> new DataDTO(entry.getValue().toString(), entry.getKey()))
            .collect(Collectors.toList());
        
        // Topic Chart
        List<DataDTO> topicChart = countTopicsByFilter(materials, filterTopic);

        // Quiz Chart
        List<Quiz> quizzes = quizRepository.findAll();
        Map<String, Long> groupedCountQuiz = quizzes.stream()
            .collect(Collectors.groupingBy(
                quiz -> getFilterValueQuiz(quiz, filterQuiz), // Group by grade or difficulty
                Collectors.counting() // Count the occurrences
            ));

        List<DataDTO> quizChart = groupedCountQuiz.entrySet().stream()
            .map(entry -> new DataDTO(entry.getValue().toString(), entry.getKey()))
            .collect(Collectors.toList());

        // User Data
        // Student
        List<User> users = userRepository.findAll();
        Map<String, Long> groupedCountUserStudent = users.stream()
            .filter(u -> "student".equalsIgnoreCase(u.getRole().toString()))
            .collect(Collectors.groupingBy(u -> getFilterValueStudent(u, filterStudent), Collectors.counting()));

        List<DataDTO> studentData = groupedCountUserStudent.entrySet().stream()
            .map(entry -> new DataDTO(entry.getValue().toString(), entry.getKey().toString()))
            .collect(Collectors.toList());
        
        // Teacher
        Map<String, Long> groupedCountUserTeacher = users.stream()
            .filter(u -> "teacher".equalsIgnoreCase(u.getRole().toString()))
            .collect(Collectors.groupingBy(
                u -> Optional.ofNullable(user.getGrade()).orElse("Unknown"),
                Collectors.counting()
            ));
        List<DataDTO> teacherData = groupedCountUserTeacher.entrySet().stream()
            .map(entry -> new DataDTO(entry.getValue().toString(), entry.getKey().toString()))
            .collect(Collectors.toList());

        UserDTO userData = new UserDTO(
            String.valueOf(users.stream()
            .filter(u -> !"ADMIN".equalsIgnoreCase(u.getRole().toString()))
            .count()),
            studentData,
            teacherData
        );

        AdminDashboardDTO body = new AdminDashboardDTO(
            // private List<DataDTO> materialChart;
            materialChart,
            
            // private List<DataDTO> TopicChart;
            topicChart,
            // private List<DataDTO> QuizChart;
            quizChart,
            // private String latestMaterial;
            user.getLastMaterial(),
            // private String latestTopic;
            user.getLastTopic(),
            // private String latestQuiz;
            user.getLastQuiz(),
            // private userDTO userData;
            userData
        );

        return ApiResponse.success(body);
    }
}
