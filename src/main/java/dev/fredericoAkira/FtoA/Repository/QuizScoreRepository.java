package dev.fredericoAkira.FtoA.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.QuizScore;

@Repository
public interface QuizScoreRepository extends MongoRepository<QuizScore, ObjectId> {
    Optional<QuizScore> findByUserIdAndQuizId(String userId, String quizId);
    List<QuizScore> getScoresByUserId(String userId);
    List<QuizScore> findByUserId(String userId);
}
