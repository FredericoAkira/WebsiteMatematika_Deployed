package dev.fredericoAkira.FtoA.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.fredericoAkira.FtoA.Entity.UserAccessLog;


@Repository
public interface UserAccessRepository extends MongoRepository<UserAccessLog, ObjectId> {
    Optional<UserAccessLog> findByUserId(String userId);
    List<UserAccessLog> findByMaterialAccessedIn(List<String> materialAccessed);
}
