package dev.fredericoAkira.FtoA.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizListDTO {
    private String quizId;
    private String quizName;
    private String difficulty;
    private String grade;
    private Double progress;
    private int latestScore;
    private int averageScore;
}
