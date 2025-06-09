package dev.fredericoAkira.FtoA.DTO;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.fredericoAkira.FtoA.DTO.DashboardDTO.User.RecentMaterialDTO;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.User.RecentQuizDTO;
import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.StudentListDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class StudentDashboardDTO {
    private List<DataDTO> quizChart;
    private List<DataDTO> materialChart;
    private RecentMaterialDTO latestMaterial;
    private RecentQuizDTO latestQuiz;
    private List<StudentListDTO> studentList = new ArrayList<>();
    private Boolean doDaily;

    // private List<MaterialListDTO> accessedMaterialNames;
}



