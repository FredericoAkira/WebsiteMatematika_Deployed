package dev.fredericoAkira.FtoA.DTO.MaterialDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.fredericoAkira.FtoA.DTO.LovDTO.LovDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MaterialDetailAdminDTO {
    private String materialId;
    private String materialName;
    private String description;
    private String backgroundImg;
    private String grade;
    private String difficulty;
    private List<LovDTO> topics;
    private List<LovDTO> quizzes;
}
