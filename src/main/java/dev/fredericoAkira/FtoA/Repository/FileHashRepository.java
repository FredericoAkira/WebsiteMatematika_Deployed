package dev.fredericoAkira.FtoA.Repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import dev.fredericoAkira.FtoA.Entity.FileHashes;

public interface FileHashRepository extends MongoRepository<FileHashes, ObjectId> {
    Optional<FileHashes> findByFileHash(String hash);
}

