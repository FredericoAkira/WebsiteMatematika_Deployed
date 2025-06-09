package dev.fredericoAkira.FtoA.Repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.QuestionReport;

@Repository
public interface QuestionReportRepository extends MongoRepository<QuestionReport, ObjectId> {
    Optional<QuestionReport> findByUserIdAndQuizIdAndQuestionId(String userId, String quizId, String questionId);

}
