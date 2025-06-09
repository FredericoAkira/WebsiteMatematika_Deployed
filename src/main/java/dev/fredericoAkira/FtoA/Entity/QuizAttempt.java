package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("quiz_attempt")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttempt {
    @Id
    private ObjectId attemptId;
    private String userId;
    private String quizId;
    private List<String> questionIds;
    private List<Answers> answeredQuestion;
}
