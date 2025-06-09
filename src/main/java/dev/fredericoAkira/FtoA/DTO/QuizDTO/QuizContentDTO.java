package dev.fredericoAkira.FtoA.DTO.QuizDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizContentDTO {
    private String questionId;
    private String question;
    private String image;
    private List<String> options;
    private String correctAnswer;
    private String difficulty;
    private SolutionDTO solution;
}
