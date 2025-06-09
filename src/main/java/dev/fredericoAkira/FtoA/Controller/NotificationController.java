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
import dev.fredericoAkira.FtoA.DTO.RequestDTO.DoubleStringReq;
import dev.fredericoAkira.FtoA.Entity.Notification;
import dev.fredericoAkira.FtoA.Service.Notification.NotificationService;

@RestController
@RequestMapping("/api")
public class NotificationController {
    
    @Autowired
    NotificationService notificationService;

    @PostMapping("/addNotification")
    public ResponseEntity<ApiResponse<?>>addNotif(@RequestBody Notification request){
        return notificationService.createNotification(request);
    }

    @PostMapping("/updateNotification")
    public ResponseEntity<ApiResponse<?>>updateNotif(@RequestBody DoubleStringReq request){
        return ResponseEntity.ok(notificationService.updateNotification(request.getItemTwo(), request.getItemOne()));
    }

    @GetMapping("/notification")
    public ResponseEntity<ApiResponse<?>>getNotif(@RequestParam String userId){
        return ResponseEntity.ok(notificationService.getNotification(userId));
    }
}
