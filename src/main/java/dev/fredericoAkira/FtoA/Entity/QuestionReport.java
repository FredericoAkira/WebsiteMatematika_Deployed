package dev.fredericoAkira.FtoA.Entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "question_report")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReport {
    @Id
    private ObjectId reportId;

    private String userId;
    private String quizId;
    private String questionId;
    private int correctCount;
    private int falseCount;
}
