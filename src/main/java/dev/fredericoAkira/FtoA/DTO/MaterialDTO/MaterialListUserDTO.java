package dev.fredericoAkira.FtoA.DTO.MaterialDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaterialListUserDTO {
    private String materialName;
    private String description;
    private String grade;
    private List<String> topics;
    private String difficulty;
}
