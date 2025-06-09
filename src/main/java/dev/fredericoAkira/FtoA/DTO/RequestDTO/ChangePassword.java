package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {
    private String userId;
    private String oldPassword;
    private String newPassword;
}
