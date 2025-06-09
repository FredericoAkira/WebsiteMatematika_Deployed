package dev.fredericoAkira.FtoA.DTO.QuizDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizDetailDTO {
    private String quizId;
    private String quizName;
    private List<?> quizContent;
    private String grade;
    private String difficulty;
}
