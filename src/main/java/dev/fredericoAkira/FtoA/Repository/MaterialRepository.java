package dev.fredericoAkira.FtoA.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.Material;

@Repository
public interface MaterialRepository extends MongoRepository<Material, ObjectId> {
    Optional<Material> findByMaterialName(String materialName);

    @Query(value = "{}", fields = "{'grade': 1, '_id': 0}")
    List<String> findAllGrades();

    List<Material> findByTopicsContaining(String topicId);
    List<Material> findByQuizzesContaining(String quizId);
    List<Material> findByMaterialIdIn(List<String> ids);
    List<Material> findByQuizzesIn(List<String>ids);
    List<Material> findByGrade(String grade);
    Optional<Material> findByQuizzesContains(String quizId);
}
