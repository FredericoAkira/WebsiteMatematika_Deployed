package dev.fredericoAkira.FtoA.DTO.TopicDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetail {
    private String topicId;
    private List<?> topicContent;
}
