package dev.fredericoAkira.FtoA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.MaterialEditReq;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.MaterialReqDTO;
import dev.fredericoAkira.FtoA.Entity.Role;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Service.MaterialService;
import dev.fredericoAkira.FtoA.Service.UserService;


@RestController
@RequestMapping("/api")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserService userService;

    @PostMapping("/admin/addMaterial")
    public ResponseEntity<ApiResponse<?>> addMaterial(@RequestBody MaterialReqDTO request) {
        return ResponseEntity.ok(materialService.addMaterial(request.getMaterial(), request.getUserId()));
    }

    @PutMapping("/admin/editMaterial")
    public ResponseEntity<?> editMaterial(@RequestBody MaterialEditReq material){
        return ResponseEntity.ok(materialService.editMaterial(material));
    }

    @DeleteMapping("/admin/deleteMaterial/{materialId}")
    public ResponseEntity<?> deleteMaterial(@PathVariable String materialId){
        return ResponseEntity.ok(materialService.deleteMaterial(materialId));
    }

    @GetMapping("/admin/getMaterialDetail")
    public ResponseEntity<?> getMaterialDetail(String materialName) {
        return ResponseEntity.ok(materialService.getMaterialDetailAdmin(materialName));
    }
    
    @GetMapping("/getMaterial")
    public ResponseEntity<ApiResponse<?>> getMaterialList(
        @RequestParam String userId,
        @RequestParam(required = false) String content,
        @RequestParam(required = false) String gradeFilter,
        @RequestParam(required = false) String difficultyFilter,
        @RequestParam(required = false) String searchQuery,
        Pageable pageable
    ) {
        User user = (User) userService.getDetailbyId(userId).getData();
        String role = user.getRole().toString();

        if(role.equals(Role.ADMIN.toString())){
            return ResponseEntity.ok(materialService.getMaterialListAdmin(userId, gradeFilter, searchQuery, pageable));
        }
        else{
            if(content.equalsIgnoreCase("quiz")){
                return ResponseEntity.ok(materialService.getMaterialListQuiz(userId, gradeFilter, difficultyFilter, searchQuery, pageable));
            }
            return ResponseEntity.ok(materialService.getMaterialList(userId, gradeFilter, difficultyFilter, searchQuery, pageable));
        }
    }

    @GetMapping("/getMaterialDetail")
    public ResponseEntity<?> getMaterialDetailUser (@RequestParam String materialName) {
        return ResponseEntity.ok(materialService.getMaterialDetail(materialName));
    }
    
}
