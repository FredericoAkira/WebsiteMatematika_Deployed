package dev.fredericoAkira.FtoA.Entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "materials")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Material {
    @Id
    private ObjectId materialId;
    @NonNull
    private String materialName;
    private String backgroundImg;
    private List<String> topics = new ArrayList<>();
    private List<String> quizzes = new ArrayList<>();
    private String difficulty;
    private String description;
    private String grade;
    private int accessCount = 0;
}
