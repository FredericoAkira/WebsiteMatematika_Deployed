package dev.fredericoAkira.FtoA.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDoItem {
    @Id
    @Field("_id")
    private String itemId;
    private String text;
    private boolean completed;
}
