package dev.fredericoAkira.FtoA.DTO.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private String username;
    private String profilePhoto;
    private String email;
    private String level;
    private int exp;
    private String grade;
    private int streak;
}
