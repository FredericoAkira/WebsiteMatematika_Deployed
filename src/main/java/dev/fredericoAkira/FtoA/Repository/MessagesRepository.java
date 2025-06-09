package dev.fredericoAkira.FtoA.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.Messages;

@Repository
public interface MessagesRepository extends MongoRepository<Messages, ObjectId> {
    List<Messages> findByGroupIdOrderByTimestampAsc(String groupId);
}

