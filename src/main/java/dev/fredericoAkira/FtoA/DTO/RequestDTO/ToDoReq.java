package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import dev.fredericoAkira.FtoA.Entity.ToDoItem;
import lombok.Data;

@Data
public class ToDoReq {
    private String userId;
    private ToDoItem toDoItem;
}
