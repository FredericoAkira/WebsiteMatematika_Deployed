package dev.fredericoAkira.FtoA.DTO.DashboardDTO;

import java.util.List;

import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String totalUser;
    private List<DataDTO> student;
    private List<DataDTO> teacher;
}
