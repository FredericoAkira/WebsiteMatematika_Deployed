package dev.fredericoAkira.FtoA.Service.LOV;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LovDTO.LovDTO;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.TopicRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Service
public class LOVService {
    @Autowired
    TopicRepository topicRepository;

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    UserRepository userRepository;

    public static List<Map.Entry<String, String>> getGradeList() {
        return List.of(
            new AbstractMap.SimpleEntry<>("1 SMP", "7"),
            new AbstractMap.SimpleEntry<>("2 SMP", "8"),
            new AbstractMap.SimpleEntry<>("3 SMP", "9"),
            new AbstractMap.SimpleEntry<>("1 SMA", "10"),
            new AbstractMap.SimpleEntry<>("2 SMA", "11"),
            new AbstractMap.SimpleEntry<>("3 SMA", "12")
        );
    }

    public static List<Map.Entry<String, String>> getFilterList() {
        return List.of(
            new AbstractMap.SimpleEntry<>("grade", "Grade"),
            new AbstractMap.SimpleEntry<>("difficulty", "Difficulty")
        );
    }

    public static List<Map.Entry<String, String>> getLevelList() {
        return List.of(
            new AbstractMap.SimpleEntry<>("Novice", "Novice"),
            new AbstractMap.SimpleEntry<>("Intermediate", "Intermediate"),
            new AbstractMap.SimpleEntry<>("Expert", "Expert")
        );
    }

    public static List<Map.Entry<String, String>> getUserLevelList() {
        return List.of(
            new AbstractMap.SimpleEntry<>("Rookie", "Rookie"),
            new AbstractMap.SimpleEntry<>("Explorer", "Explorer"),
            new AbstractMap.SimpleEntry<>("Challenger", "Challenger"),
            new AbstractMap.SimpleEntry<>("Master", "Master")
        );
    }

    private static final Set<String> VALID_TYPES =
        Set.of(
            "topiclov",
            "quizlov",
            "gradelov",
            "filterlov",
            "materiallov",
            "levellov",
            "studentlov",
            "userlevellov"
        );

    public boolean isValidType(String type) {
        return type != null && VALID_TYPES.contains(type.toLowerCase());
    }

    public ApiResponse<List<LovDTO>> getLovByType(String type, String search) {
        List<LovDTO> result = switch (type.toLowerCase()) {
            case "topiclov" -> topicRepository.findAll().stream()
                .map(t -> new LovDTO(t.getTopicName(), t.getTopicId().toString()))
                .collect(Collectors.toList());
    
            case "quizlov" -> quizRepository.findAll().stream()
                .map(q -> new LovDTO(q.getQuizName(), q.getQuizId().toString()))
                .collect(Collectors.toList());
    
            case "gradelov" -> getGradeList().stream()
                .map(q -> new LovDTO(q.getValue(), q.getKey()))
                .collect(Collectors.toList());
            
            case "filterlov" -> getFilterList().stream()
                .map(q -> new LovDTO(q.getValue(), q.getKey()))
                .collect(Collectors.toList());

            case "materiallov" -> materialRepository.findAll().stream()
                .map(q -> new LovDTO(q.getMaterialName(), q.getMaterialId().toString()))
                .collect(Collectors.toList());

            case "levellov" -> getLevelList().stream()
                .map(q -> new LovDTO(q.getValue(), q.getKey()))
                .collect(Collectors.toList());

            case "studentlov" -> userRepository.findAll().stream()
                .filter(u -> u.getRole().toString().equalsIgnoreCase("student"))
                .map(q -> new LovDTO(q.getUsername(), q.getUserId().toString()))
                .collect(Collectors.toList());
            
            case "userlevellov" -> getUserLevelList().stream()
                .map(q -> new LovDTO(q.getValue(), q.getKey()))
                .collect(Collectors.toList());

    
            default -> List.of(); // won't happen if controller validates type
        };
    
        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase();
            result = result.stream()
                .filter(lov ->
                    (lov.getName() != null && lov.getName().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
        }
    
        return ApiResponse.success(result);
    }
}
