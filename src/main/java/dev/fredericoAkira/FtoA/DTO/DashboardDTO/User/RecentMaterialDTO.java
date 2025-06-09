package dev.fredericoAkira.FtoA.DTO.DashboardDTO.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentMaterialDTO {
    private String materialName;
    private String difficulty;
    private String grade;
}
