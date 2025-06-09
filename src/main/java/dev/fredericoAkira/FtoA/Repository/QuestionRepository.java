package dev.fredericoAkira.FtoA.Repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.Question;

@Repository
public interface QuestionRepository extends MongoRepository<Question, ObjectId> {
    
}
