package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupSTD {
    private String studentId;
    private String studentName;
    private String level;
    private String grade;
}
