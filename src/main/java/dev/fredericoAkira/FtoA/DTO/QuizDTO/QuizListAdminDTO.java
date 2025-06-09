package dev.fredericoAkira.FtoA.DTO.QuizDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizListAdminDTO {
    private String id;
    private String materialName[];
    private String quizName;
    private String difficulty;
    private String grade;
    private String totalContent;
}
