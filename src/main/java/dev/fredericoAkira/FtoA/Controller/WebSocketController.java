package dev.fredericoAkira.FtoA.Controller;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.fredericoAkira.FtoA.Entity.Messages;
import dev.fredericoAkira.FtoA.Repository.MessagesRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Controller
@RequestMapping("/api")
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.send/{groupId}")
    public void sendMessage(@DestinationVariable String groupId, Messages message) {
        message.setTimestamp(new Date());
        message.setGroupId(groupId);

        userRepository.findById(new ObjectId(message.getSenderId())).ifPresent(user ->
            message.setSenderName(user.getUsername())
        );

        messagesRepository.save(message);

        // broadcast to clients
        messagingTemplate.convertAndSend("/topic/messages/" + groupId, message);
    }
}
