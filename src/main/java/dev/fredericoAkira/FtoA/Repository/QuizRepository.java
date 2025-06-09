package dev.fredericoAkira.FtoA.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.Quiz;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, ObjectId>  {
    Optional<Quiz> findByQuizName(String quizName);
    List<Quiz> findByGradeAndDifficulty(String userGrade, String userDifficulty);
}
