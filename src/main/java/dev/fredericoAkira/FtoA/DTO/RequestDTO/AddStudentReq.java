package dev.fredericoAkira.FtoA.DTO.RequestDTO;

import java.util.List;

import lombok.Data;

@Data
public class AddStudentReq {
    private String teacherId;
    private List<String> studentIds;
}
