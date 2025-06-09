package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import java.util.List;

import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDTO {
    private String groupId;
    private String groupName;
    private List<DataDTO> students;
}
