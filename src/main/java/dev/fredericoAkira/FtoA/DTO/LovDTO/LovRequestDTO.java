package dev.fredericoAkira.FtoA.DTO.LovDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LovRequestDTO {
    private String type;
    private String search;
}
