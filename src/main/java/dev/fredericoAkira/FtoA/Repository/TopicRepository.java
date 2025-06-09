package dev.fredericoAkira.FtoA.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.Topic;

@Repository
public interface TopicRepository extends MongoRepository<Topic, ObjectId>{
    List<Topic> findByTopicIdIn(List<String> topicIds);
    Optional<Topic> findByTopicName(String topicName);
    Boolean existsByTopicName(String topicName);
    List<Topic> findByTopicNameContainingIgnoreCase(String topicName);

    @Query(value = "{}", fields = "{ 'topicName': 1, '_id': 1, 'topicContent': 1 }")
    Page<Topic> findAllBy(Pageable pageable);

}
