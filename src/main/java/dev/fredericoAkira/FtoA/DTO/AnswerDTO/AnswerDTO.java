package dev.fredericoAkira.FtoA.DTO.AnswerDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private String userAnswer;
    private Boolean isCorrect;
    private String correctAnswer;
}
