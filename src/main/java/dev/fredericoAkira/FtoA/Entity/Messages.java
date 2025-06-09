package dev.fredericoAkira.FtoA.Entity;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Messages {
    @Id
    private ObjectId id;

    private String groupId;
    private String senderId;
    private String senderName;
    private String content;
    private Date timestamp = new Date();

    private boolean edited = false;
    private boolean deleted = false;
}
