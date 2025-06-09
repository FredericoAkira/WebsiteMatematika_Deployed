package dev.fredericoAkira.FtoA.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LovDTO.LovDTO;
import dev.fredericoAkira.FtoA.DTO.LovDTO.LovRequestDTO;
import dev.fredericoAkira.FtoA.Service.LOV.LOVService;


@RestController
public class LOVController {
    @Autowired
    LOVService lovService;

    @PostMapping("/api/list-of-values")
    public ResponseEntity<ApiResponse<?>> getMethodName(@RequestBody LovRequestDTO requestDTO) {
        if (!lovService.isValidType(requestDTO.getType())) {
            return ResponseEntity.badRequest().build();
        }

        ApiResponse<List<LovDTO>> result = lovService.getLovByType(requestDTO.getType(), requestDTO.getSearch());
        return ResponseEntity.ok(result);
    }
    
}
