package dev.fredericoAkira.FtoA.DTO.QuizDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResultDTO {
    private int pointsEarned;
    private double correctPercentage;
    private int updatedExp;
}
