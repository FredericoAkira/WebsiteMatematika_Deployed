package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelUpReq {
    private String userId;
    private int totalProgress;
    private int levelCap;
    private String currentLevel;
}
