package dev.fredericoAkira.FtoA.Entity;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "daily_recommendations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "unique_user_date", def = "{'userId': 1, 'date': 1}", unique = true)
public class DailyRecommendation {
    @Id
    private ObjectId id;
    @NonNull
    private String userId;
    @NonNull
    private LocalDate date;
    private List<String> questionIds;
}
