package dev.fredericoAkira.FtoA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.ToDoReq;
import dev.fredericoAkira.FtoA.Entity.ToDo;
import dev.fredericoAkira.FtoA.Service.ToDo.ToDoService;

@RestController
@RequestMapping("/api")
public class ToDoController {
    @Autowired
    ToDoService toDoService;

    @GetMapping("/getToDoList")
    public ResponseEntity<ApiResponse<ToDo>> getTodos(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success(toDoService.getTodos(userId)));
    }

    @PostMapping("/addToDos")
    public ResponseEntity<ApiResponse<?>> addTodo(@RequestBody ToDoReq todo) {
        toDoService.addTodo(todo.getUserId(), todo.getToDoItem());
        return ResponseEntity.ok(ApiResponse.success("Todo successfully added"));
    }

    @PutMapping("/editToDos")
    public ResponseEntity<ApiResponse<?>> updateTodo(@RequestBody ToDoReq todo) {
        toDoService.updateTodo(todo.getUserId(), todo.getToDoItem());
        return ResponseEntity.ok(ApiResponse.success("Todo successfully edited"));
    }

    @DeleteMapping("/deleteToDos/{userId}/{todoId}")
    public ResponseEntity<ApiResponse<?>> deleteTodo(@PathVariable String userId, @PathVariable String todoId) {
        toDoService.deleteTodo(userId, todoId);
        return ResponseEntity.ok(ApiResponse.success("Todo successfully deleted"));
    }
}
