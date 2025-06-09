package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "quizes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    private ObjectId quizId;
    private String quizName;
    private List<String> quizContent;
    private String grade;
    private String difficulty;
    private int accessCount = 0;
     // For receiving/updating
     @Transient
     private List<Question> questions;
}

