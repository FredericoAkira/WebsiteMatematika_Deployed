package dev.fredericoAkira.FtoA.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelUpDTO {
    private String newLevel;
    private String message;
    private Boolean levelChanged;
}
