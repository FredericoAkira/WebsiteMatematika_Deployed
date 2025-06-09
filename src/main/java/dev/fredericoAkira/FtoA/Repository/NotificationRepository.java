package dev.fredericoAkira.FtoA.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import dev.fredericoAkira.FtoA.Entity.Notification;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
        List<Notification> findByReceiverIdAndStatusIn(String receiverId, List<String> statuses);
        List<Notification> findAllByReceiverId(String receiverId);
}
