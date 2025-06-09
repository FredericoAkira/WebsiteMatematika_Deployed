package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentListDTO {
    private String studentName;
    private String grade;
}
