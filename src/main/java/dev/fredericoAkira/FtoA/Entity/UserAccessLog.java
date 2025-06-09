package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("access_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessLog {
    @Id
    private ObjectId AccessId;
    private String userId;
    private List<String> materialAccessed;
    private List<String> quizAccessed;
}
