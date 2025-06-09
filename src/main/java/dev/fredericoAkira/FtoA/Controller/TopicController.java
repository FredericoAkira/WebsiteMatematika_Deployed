package dev.fredericoAkira.FtoA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.DTO.RequestDTO.TopicReqDTO;
import dev.fredericoAkira.FtoA.Entity.Topic;
import dev.fredericoAkira.FtoA.Service.TopicService;

@RestController
@RequestMapping("/api")
public class TopicController {
    @Autowired
    private TopicService topicService;


    @PostMapping("/admin/addTopic")
    public ResponseEntity<?> addTopic(@RequestBody TopicReqDTO topic){
        return ResponseEntity.ok(topicService.addTopic(topic.getTopic(), topic.getUserId()));
    }

    @GetMapping("/admin/getTopic")
    public ResponseEntity<?> getAllTopicNames(Pageable pageable, @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(topicService.getAllTopicNames(pageable, materialName, search));
    }

    @PutMapping("/admin/editTopic")
    public ResponseEntity<?> editTopic(@RequestBody Topic topic){
        return ResponseEntity.ok(topicService.editTopic(topic));
    }

    @DeleteMapping("/admin/deleteTopic/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable String topicId){
        return ResponseEntity.ok(topicService.deleteTopic(topicId));
    }

    @GetMapping("/admin/getTopicDetail")
    public ResponseEntity<?> getTopicDetail(@RequestParam String topicName) {
        return ResponseEntity.ok(topicService.accessTopic(topicName));
    }
}
