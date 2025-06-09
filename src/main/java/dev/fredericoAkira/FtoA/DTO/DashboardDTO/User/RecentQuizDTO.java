package dev.fredericoAkira.FtoA.DTO.DashboardDTO.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentQuizDTO {
    private String quizName;
    private String grade;
    private Double progress;
    private String difficulty;
    private String materialName;
}
