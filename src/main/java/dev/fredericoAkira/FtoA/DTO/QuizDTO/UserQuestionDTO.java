package dev.fredericoAkira.FtoA.DTO.QuizDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UserQuestionDTO {
    private String questionId;
    private String question;
    private String image;
    private List<String> options;
}
