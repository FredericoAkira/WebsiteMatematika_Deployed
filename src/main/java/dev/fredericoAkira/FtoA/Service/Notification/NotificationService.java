package dev.fredericoAkira.FtoA.Service.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.NotificationDTO.NotificationDetail;
import dev.fredericoAkira.FtoA.Entity.Notification;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.NotificationRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ApiResponse<?>> createNotification(Notification request){
        Notification notification = new Notification();
        notification.setSenderId(request.getSenderId());
        notification.setReceiverId(request.getReceiverId());
        notification.setStatus(request.getStatus());
        notification.setHeader(request.getHeader());
        notification.setContent(request.getContent());

        notificationRepository.save(notification);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    public ApiResponse<?> updateNotification(String status, String notifId){
        Notification notification = notificationRepository.findById(new ObjectId(notifId)).orElseThrow(() -> new RuntimeException("Notification not Found"));
        String previousStatus = notification.getStatus();

        notification.setStatus(status);

        if ((previousStatus.equals("OPEN - AS") || previousStatus.equals("PENDING - AS"))
            && status.equals("ACCEPTED")) {

            String teacherId = notification.getSenderId(); // Asumsikan sender adalah guru
            String studentId = notification.getReceiverId();   // Asumsikan receiver adalah siswa

            User teacher = userRepository.findById(new ObjectId(teacherId))
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

            if (teacher.getStudents() == null) {
                teacher.setStudents(new ArrayList<>());
            }

            if (!teacher.getStudents().contains(studentId)) {
                teacher.getStudents().add(studentId);
                userRepository.save(teacher); // Simpan update ke list students
            }
        }

        notificationRepository.save(notification);

        return ApiResponse.success("Status Updated Successfully");
    }

    public ApiResponse<List<NotificationDetail>> getNotification(String userId){
        List<String> statuses = Arrays.asList("OPEN", "PENDING", "OPEN - AS", "PENDING - AS", "groupChat");
        List<Notification> notifications = notificationRepository.findByReceiverIdAndStatusIn(userId, statuses);
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));

        List<NotificationDetail> details = notifications.stream()
            .map(notification -> {
                // convert Notification to NotificationDetail
                NotificationDetail detail = new NotificationDetail(
                    notification.getNotifId().toString(),
                    notification.getHeader(),
                    notification.getContent(),
                    user.getUsername(),
                    notification.getStatus()
                );
                return detail;
            })
        .collect(Collectors.toList());
        
        return ApiResponse.success(details);
    }
}
