package dev.fredericoAkira.FtoA.DTO.DashboardDTO;

import java.util.List;

import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {
    private List<DataDTO> materialChart;
    private List<DataDTO> TopicChart;
    private List<DataDTO> QuizChart;
    private String latestMaterial;
    private String latestTopic;
    private String latestQuiz;
    private UserDTO userData;
}
