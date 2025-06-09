package dev.fredericoAkira.FtoA.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LovDTO.LovDTO;
import dev.fredericoAkira.FtoA.DTO.TopicDTO.TopicDetail;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Topic;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.TopicRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Util.PropertyCopyUtil;

@Service
public class TopicService {
    @Autowired
    TopicRepository topicRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<ApiResponse<?>> addTopic(Topic request, String userId){
        boolean exists = topicRepository.existsByTopicName(request.getTopicName());
    
        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic with this name already exists.");
        }

        Topic topic = new Topic();
        topic.setTopicName(request.getTopicName());
        topic.setTopicContent(request.getTopicContent());

        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        user.setLastTopic(request.getTopicName());
        userRepository.save(user);

        topicRepository.save(topic);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    public ApiResponse<?> editTopic(Topic request){
        Topic topic = topicRepository.findById(request.getTopicId()).orElseThrow(() -> new RuntimeException("Topic not Found"));
        PropertyCopyUtil.copyNonNullProperties(request, topic);

        topicRepository.save(topic);

        return ApiResponse.success("Topic Updated Successfully");
    }

    public ApiResponse<?> deleteTopic(String topicId){
        Topic topic = topicRepository.findById(new ObjectId(topicId)).orElseThrow(() -> new RuntimeException("Topic not Found"));
        
        Query query = new Query(Criteria.where("topics").is(topicId));
        Update update = new Update().pull("topics", topicId);
        mongoTemplate.updateMulti(query, update, Material.class);
        
        topicRepository.delete(topic);

        return ApiResponse.success("Topic Deleted Successfully");
    }

    public ApiResponse<?> removeTopicFromMaterial(String topicId, String materialId){
        // Use MongoTemplate to remove the topicId from the topics list
        Query query = new Query(Criteria.where("_id").is(new ObjectId(materialId)));
        Update update = new Update().pull("topics", new ObjectId(topicId));
        mongoTemplate.updateFirst(query, update, Material.class);

        return ApiResponse.success("Topic removed from Material successfully");
    }

    public ApiResponse<TopicDetail> accessTopic(String topicName){
        System.out.println("topicName: " + topicName);
        Topic topic = topicRepository.findByTopicName(topicName).orElseThrow(() -> new RuntimeException("Topic not Found"));
        TopicDetail body = new TopicDetail(
            topic.getTopicId().toString(),
            topic.getTopicContent()
        );
        return ApiResponse.success(body);
    }

    public ApiResponse<Page<LovDTO>> getAllTopicNames(
        Pageable pageable,
        @RequestParam(required = false) String materialName,
        @RequestParam(required = false) String search
    ){
        List<Topic> topics;
        // Apply search filtering if `search` is provided
        if (search != null && !search.isEmpty()) {
            topics = topicRepository.findByTopicNameContainingIgnoreCase(search);
        } else {
            topics = topicRepository.findAll();
        }

        List<LovDTO> lovDTOList = new ArrayList<>();

        for (Topic topic : topics) {
            String topicId = topic.getTopicId() != null ? topic.getTopicId().toString() : "-";
            String totalContent = String.valueOf(topic.getTopicContent() != null ? topic.getTopicContent().size() : 0);

            List<Material> materials = topic.getTopicId() != null
                ? materialRepository.findByTopicsContaining(topicId)
                : Collections.emptyList();
            
            if (materialName != null && materialName.trim().isEmpty()) {
                materialName = null;
            }

            if (materials.isEmpty()) {
                // Only show default "-" if no materials at all AND no filter is applied
                if (materialName == null) {
                    lovDTOList.add(new LovDTO(topic.getTopicName(), topicId, totalContent, "-"));
                }
            } else {
                for (Material material : materials) {
                    // If no filter, show all materials
                    // If filter is set, show only the ones that match (case-insensitive, trimmed)
                    System.out.println("material name dari luar" + materialName );
                    if (materialName == null ||
                        material.getMaterialName().equalsIgnoreCase(materialName)) {
                        
                        lovDTOList.add(new LovDTO(
                            topic.getTopicName(),
                            topicId,
                            totalContent,
                            material.getMaterialName()
                        ));
                    }
                }
            }
        }

        // Manually apply pagination AFTER processing
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lovDTOList.size());
        List<LovDTO> pagedList = lovDTOList.subList(start, end);

        Page<LovDTO> lovDTOPage = new PageImpl<>(pagedList, pageable, lovDTOList.size());

        return ApiResponse.success(lovDTOPage);
    }
}
