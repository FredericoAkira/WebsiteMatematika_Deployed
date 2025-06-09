package dev.fredericoAkira.FtoA.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuizDetailDTO {
    private String accessId;
    private String username;
    private String quizId;
    private List<QuestionAnswer> answers;
}

@Data
class QuestionAnswer{
    private String questionId;
    private String answer;
}
