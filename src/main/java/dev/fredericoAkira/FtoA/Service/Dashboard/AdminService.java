package dev.fredericoAkira.FtoA.Service.Dashboard;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.DashboardDTO.AdminDetailDTO;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passEncoder;

    public ApiResponse<AdminDetailDTO> getAdminData(String userId){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        AdminDetailDTO body = new AdminDetailDTO(
            user.getUsername(),
            user.getProfilePhoto(),
            user.getEmail()
        );
        return ApiResponse.success(body);
    }

    public ApiResponse<?> changePassword(String userId, String oldPassword, String newPassword){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        if(passEncoder.matches(oldPassword, user.getPassword())){
            user.setPassword(passEncoder.encode(newPassword));
            userRepository.save(user);
            return ApiResponse.success("Password changed succcessfully");
        } else {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Password not match");
        }
    }

    public ApiResponse<?> changeProfilePhoto(String userId, String profileUrl){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        user.setProfilePhoto(profileUrl);

        userRepository.save(user);
        return ApiResponse.success("Profile changed succcessfully");
    }

    public ApiResponse<?> changeAdminData(String userId, String username, String email){
        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        user.setUsername(username);
        user.setEmail(email);
        userRepository.save(user);
        return ApiResponse.success("Data edited successfully");
    }
}
