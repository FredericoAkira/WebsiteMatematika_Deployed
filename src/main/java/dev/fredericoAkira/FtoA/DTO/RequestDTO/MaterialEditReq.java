package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialEditReq {
    private String materialId;
    private String materialName;
    private List<String> topics = new ArrayList<>();
    private List<String> quizzes = new ArrayList<>();
    private String difficulty;
    private String description;
    private String grade;
}
