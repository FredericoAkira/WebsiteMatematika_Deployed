package dev.fredericoAkira.FtoA.Repository;

import java.time.LocalDate;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.DailyRecommendation;

@Repository
public interface DailyRecommendationRepository extends MongoRepository<DailyRecommendation, ObjectId> {
    Optional<DailyRecommendation> findByUserIdAndDate(String userId, LocalDate date);
    void deleteByDateBefore(LocalDate date); // for cleanup
}
