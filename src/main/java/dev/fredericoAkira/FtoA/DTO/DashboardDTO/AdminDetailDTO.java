package dev.fredericoAkira.FtoA.DTO.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDetailDTO {
    private String username;
    private String profilePhoto;
    private String email;
}
