package dev.fredericoAkira.FtoA.DTO.MaterialDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MaterialListAdminDTO {
    private String materialId;
    private String materialName;
    private List<String> topics;
    private List<String> quizzes;
    private String grade;
    private String difficulty;
}
