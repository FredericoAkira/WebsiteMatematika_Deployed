package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "topics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    @Id
    @Field("_id")
    private ObjectId topicId;
    private String topicName;
    private List<TopicContent> topicContent;
}


@Data
class TopicContent {
    private String explanation;
    private String text;
    private String image;
    private String audio;
    private String video;
}
