package dev.fredericoAkira.FtoA.DTO.LovDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class LovDTO {
    private String name;
    private String id;
    private String description;
    private String parent;

    public LovDTO(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
