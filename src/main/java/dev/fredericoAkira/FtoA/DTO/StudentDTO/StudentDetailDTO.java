package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import java.util.List;

import dev.fredericoAkira.FtoA.DTO.QuizListDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailDTO {
    private String studentName;
    private List<StudentMaterialDTO> accessedMaterials;
    private List<QuizListDTO> accessedQuizzes;
}
