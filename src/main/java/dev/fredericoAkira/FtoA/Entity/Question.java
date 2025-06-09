package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import dev.fredericoAkira.FtoA.DTO.QuizDTO.SolutionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    private ObjectId questionId;
    private String question;
    private String image;
    private List<String> options;
    private String correctAnswer;
    private String difficulty;
    private SolutionDTO solution;
}