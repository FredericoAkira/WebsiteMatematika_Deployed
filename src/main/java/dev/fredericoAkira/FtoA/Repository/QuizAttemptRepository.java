package dev.fredericoAkira.FtoA.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.QuizAttempt;

@Repository
public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, ObjectId>{
    Optional<QuizAttempt> findByquizId(String quizId);
    Optional<QuizAttempt> findByUserId(String userId);
    Optional<List<QuizAttempt>> findAllByUserId(String userId);
    Optional<QuizAttempt> findByUserIdAndQuizId(String userId, String quizId);
    Integer countByUserId(String userId);
    void deleteAllByQuizId(String quizId);
}
