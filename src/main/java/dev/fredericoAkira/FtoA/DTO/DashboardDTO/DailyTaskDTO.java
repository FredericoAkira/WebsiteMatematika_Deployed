package dev.fredericoAkira.FtoA.DTO.DashboardDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyTaskDTO {
    private String questionId;
    private String question;
    private String image;
    private List<String> options;
    private String difficulty;
}
