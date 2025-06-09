package dev.fredericoAkira.FtoA.Entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "todos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDo {
    @Id
    private String userId;
    private List<ToDoItem> todoItems;
}
