package dev.fredericoAkira.FtoA.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.ToDo;

@Repository
public interface ToDoRepository extends MongoRepository<ToDo, String> {

}
