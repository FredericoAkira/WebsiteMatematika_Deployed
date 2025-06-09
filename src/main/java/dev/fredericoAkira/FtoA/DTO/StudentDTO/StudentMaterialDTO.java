package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMaterialDTO {
    private String materialName;
    private String difficulty;
    private String grade;
}
