package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import dev.fredericoAkira.FtoA.Entity.Topic;
import lombok.Data;

@Data
public class TopicReqDTO {
    private Topic topic;
    private String userId;
}
