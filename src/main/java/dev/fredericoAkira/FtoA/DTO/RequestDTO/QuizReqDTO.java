package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import dev.fredericoAkira.FtoA.Entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizReqDTO {
    private Quiz quiz;
    private String userId;
}
