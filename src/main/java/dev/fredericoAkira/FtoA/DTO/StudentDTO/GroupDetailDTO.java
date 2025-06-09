package dev.fredericoAkira.FtoA.DTO.StudentDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailDTO {
    private String groupName;
    private List<GroupSTD> students;
}
