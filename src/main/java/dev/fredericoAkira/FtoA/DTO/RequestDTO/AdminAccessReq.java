package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAccessReq {
    private String userId;
    private String type;
    private String itemName;
}
