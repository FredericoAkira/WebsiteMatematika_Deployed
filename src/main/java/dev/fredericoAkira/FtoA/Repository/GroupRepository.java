package dev.fredericoAkira.FtoA.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, ObjectId>{
    List<Group> findByTeacherId(String teacherId);
    List<Group> findByGroupIdIn(List<ObjectId> groupIds);
}
