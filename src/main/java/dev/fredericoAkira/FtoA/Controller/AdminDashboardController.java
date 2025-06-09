package dev.fredericoAkira.FtoA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.AdminAccessReq;
import dev.fredericoAkira.FtoA.Service.UserService;
import dev.fredericoAkira.FtoA.Service.Dashboard.DashboardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminDashboardController {

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;
    
    // Used - Admin Dashboard
    @GetMapping("/admin/dashboardData")
    public ResponseEntity<ApiResponse<?>> getDashboard (
        @RequestParam String userId,
        @RequestParam(required = false) String filterMaterial,
        @RequestParam(required = false) String filterTopic,
        @RequestParam(required = false) String filterQuiz,
        @RequestParam(required = false) String filterStudent
    ) {
        return ResponseEntity.ok(dashboardService.adminDashboard(userId, filterMaterial, filterTopic, filterQuiz, filterStudent));
    }

    // Used - Admin Dashboard
    @PostMapping("/admin/access")
    public ResponseEntity<ApiResponse<?>> accessContent (
        @RequestBody AdminAccessReq request
    ){
        return ResponseEntity.ok(userService.setLatestAccess(request.getType(), request.getUserId(), request.getItemName()));
    }

}
