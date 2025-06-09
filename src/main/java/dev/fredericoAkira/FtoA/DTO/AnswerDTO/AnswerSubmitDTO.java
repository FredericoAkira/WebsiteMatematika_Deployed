package dev.fredericoAkira.FtoA.DTO.AnswerDTO;

import lombok.Data;

@Data
public class AnswerSubmitDTO {
    private String attemptId;
    private String questionId;
    private String userAnswer;
}
