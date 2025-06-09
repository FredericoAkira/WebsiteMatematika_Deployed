package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizScore {
    @Id
    private ObjectId scoreId;
    private String userId;
    private String quizId;
    private List<String> scores;
}
