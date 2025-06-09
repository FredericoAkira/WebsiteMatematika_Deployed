package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTableDTO {
    private String studentId;
    private String studentName;
    private String grade;
    private String latestMaterial;
    private String latestQuiz;
    private String level;
}
