package dev.fredericoAkira.FtoA.Controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import dev.fredericoAkira.FtoA.Entity.Group;
import dev.fredericoAkira.FtoA.Entity.Messages;
import dev.fredericoAkira.FtoA.Entity.Notification;
import dev.fredericoAkira.FtoA.Repository.GroupRepository;
import dev.fredericoAkira.FtoA.Repository.MessagesRepository;
import dev.fredericoAkira.FtoA.Repository.NotificationRepository;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private GroupRepository groupRepository;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Messages message) {
        message.setTimestamp(new Date());
        Messages saved = messagesRepository.save(message);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<Messages>> getGroupMessages(@PathVariable String groupId) {
        List<Messages> messages = messagesRepository.findByGroupIdOrderByTimestampAsc(groupId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/getChatList")
    public ResponseEntity<ApiResponse<List<DataDTO>>> getChatList(@RequestParam String userId) {
        List<Notification> notif = notificationRepository.findAllByReceiverId(userId);
        List<String> groupId = notif.stream()
            .filter(n -> "groupChat".equals(n.getStatus()))
            .map(Notification::getSenderId)
            .distinct()
            .collect(Collectors.toList());
        List<ObjectId> objectIds = groupId.stream()
        .filter(ObjectId::isValid) // optional safety check
        .map(ObjectId::new)
        .toList();

        // Find matching groups
        List<Group> matchingGroups = groupRepository.findByGroupIdIn(objectIds);
        List<DataDTO> groupDTOs = matchingGroups.stream()
            .map(group -> new DataDTO(group.getGroupId().toString(), group.getGroupName()))
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(groupDTOs));
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteMessage(@PathVariable String id) {
    //     Optional<Messages> optionalMessage = messagesRepository.findById(new ObjectId(id));
    //     if (optionalMessage.isPresent()) {
    //         Messages message = optionalMessage.get();
    //         message.setDeleted(true);
    //         message.setContent("This message was deleted.");
    //         messagesRepository.save(message);

    //         messagingTemplate.convertAndSend("/topic/messages/" + message.getGroupId(), message);
    //         return ResponseEntity.ok("Message deleted");
    //     }
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<?> editMessage(@PathVariable String id, @RequestBody String newContent) {
    //     Optional<Messages> optionalMessage = messagesRepository.findById(new ObjectId(id));
    //     if (optionalMessage.isPresent()) {
    //         Messages message = optionalMessage.get();
    //         message.setContent(newContent);
    //         message.setEdited(true);
    //         messagesRepository.save(message);

    //         messagingTemplate.convertAndSend("/topic/messages/" + message.getGroupId(), message);
    //         return ResponseEntity.ok("Message edited");
    //     }
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
    // }
}
