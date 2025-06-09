package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import dev.fredericoAkira.FtoA.Entity.Material;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialReqDTO {
    private Material material;
    private String userId;
}
