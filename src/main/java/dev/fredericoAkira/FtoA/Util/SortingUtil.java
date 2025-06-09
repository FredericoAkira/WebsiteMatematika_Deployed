package dev.fredericoAkira.FtoA.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.fredericoAkira.FtoA.Entity.Quiz;

public class SortingUtil {
    // Sorting logic that accepts a dynamic sorting order
        /**
     * Get the sorting order for a given difficulty based on user level.
     */
    public static int getSortOrder(String difficulty, String userLevel, Map<String, List<String>> levelSortOrder) {
        List<String> order = levelSortOrder.getOrDefault(userLevel, List.of("Rookie", "Explorer", "Challenger", "Master"));
        return order.indexOf(difficulty != null ? difficulty : "Novice"); // default difficulty kalau null
    }

    /**
     * Sort quizzes based on user level and latest quiz.
     */
    public static List<Quiz> sortQuizzes(List<Quiz> quizzes, String latestQuizId, String userLevel, Map<String, List<String>> levelSortOrder) {
        String effectiveLevel;
        if (userLevel == null) {
            System.out.println("[WARN] userLevel kosong, menggunakan default 'Novice'");
            effectiveLevel = "Novice";
        } else {
            effectiveLevel = userLevel;
        }
    
        quizzes.sort(Comparator
            // 1. Latest quiz first
            .comparing((Quiz q) -> {
                if (q.getQuizName() == null || latestQuizId == null) return false;
                return q.getQuizName().equalsIgnoreCase(latestQuizId);
            }).reversed()
    
            // 2. Then, by difficulty based on user level
            .thenComparingInt(q -> {
                String difficulty = q.getDifficulty() != null ? q.getDifficulty() : "Novice";
                return getSortOrder(difficulty, effectiveLevel, levelSortOrder);
            })
    
            // 3. (Optional) Then, by quiz name alphabetically
            .thenComparing(q -> q.getQuizName() != null ? q.getQuizName() : "")
        );
    
        return quizzes;
    }
    

    public static List<Integer> getSortedGradeOrder(int userGrade, int maxGrade) {
        // Create a list that starts from userGrade to maxGrade
        List<Integer> firstPart = IntStream.rangeClosed(userGrade, maxGrade)
            .boxed()
            .collect(Collectors.toList());

        // Add the wrap-around part from 1 to userGrade-1
        List<Integer> secondPart = IntStream.rangeClosed(1, userGrade - 1)
            .boxed()
            .collect(Collectors.toList());

        // Combine both parts
        firstPart.addAll(secondPart);
        return firstPart;
    }
}

