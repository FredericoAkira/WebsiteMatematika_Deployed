package dev.fredericoAkira.FtoA.Repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import dev.fredericoAkira.FtoA.Entity.User;


public interface UserRepository extends MongoRepository<User, ObjectId>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("{ }, { $set: { 'doDaily': ?0 } }") // MongoDB update query
    void updateDoDailyForAllUsers(boolean value);
}
