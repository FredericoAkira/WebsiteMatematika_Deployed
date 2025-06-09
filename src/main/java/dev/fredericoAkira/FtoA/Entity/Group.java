package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "teacher_groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    private ObjectId groupId;
    private String groupName;
    private String teacherId;
    private List<String> studentIds;
}
