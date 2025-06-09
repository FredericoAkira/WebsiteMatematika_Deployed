package dev.fredericoAkira.FtoA.Service.ToDo;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Entity.ToDo;
import dev.fredericoAkira.FtoA.Entity.ToDoItem;
import dev.fredericoAkira.FtoA.Repository.ToDoRepository;

@Service
public class ToDoService {
    @Autowired
    private ToDoRepository repo;

    public ToDo getTodos(String userId) {
        return repo.findById(userId).orElse(new ToDo(userId, new ArrayList<>()));
    }

    public void addTodo(String userId, ToDoItem newTodo) {
        ToDo todo = getTodos(userId);
        todo.getTodoItems().add(newTodo);
        repo.save(todo);
    }

    public void updateTodo(String userId, ToDoItem updatedTodo) {
        ToDo todo = getTodos(userId);
        todo.getTodoItems().replaceAll(t -> t.getItemId().equals(updatedTodo.getItemId()) ? updatedTodo : t);
        repo.save(todo);
    }

    public void deleteTodo(String userId, String todoId) {
        ToDo todo = getTodos(userId);
        todo.getTodoItems().removeIf(t -> t.getItemId().equals(todoId));
        repo.save(todo);
    }
}
