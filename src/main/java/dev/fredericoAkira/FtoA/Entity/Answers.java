package dev.fredericoAkira.FtoA.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answers {
    private String questionId;
    private String userAnswer;
    private Boolean isCorrect;
    private String correctAnswer;
}
