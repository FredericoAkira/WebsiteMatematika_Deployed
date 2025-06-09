package dev.fredericoAkira.FtoA.Service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.Configuration.CustomException;
import dev.fredericoAkira.FtoA.DTO.AuthDTO;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository usRepo;

    @Autowired
    private PasswordEncoder passEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<?> registerUser(User request, HttpServletResponse response){

        if(usRepo.existsByUsername(request.getUsername())){
            throw new CustomException(HttpStatus.BAD_REQUEST, "Username already exists.");
        }

        if(usRepo.existsByEmail(request.getEmail())){
            throw new CustomException(HttpStatus.BAD_REQUEST, "Email already exists.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setLevel("Rookie");
        user.setDoDaily(false);

        user = usRepo.save(user);

        User findUser = usRepo.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        String userId = findUser != null ? user.getUserId().toString() : "";
        String userRole = findUser != null ? user.getRole().toString() : "";

        String token = jwtService.generateToken(user);
        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", token)
        .httpOnly(true)  // Prevent JavaScript access
        //.secure(true)    // Use only over HTTPS
        .path("/")       // Available for all endpoints
        .maxAge(Duration.ofDays(7)) // Expiry time
        .sameSite("Strict") // Prevent CSRF attacks
        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        AuthDTO responseBody = new AuthDTO(
            userId,
            userRole
        );

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(ApiResponse.success(responseBody));
    }

    public ResponseEntity<?> authenticate (User request, HttpServletResponse response){
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST,"Username atau password salah"));
        }

        User user = usRepo.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);

        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", token)
        .httpOnly(true)  // Prevent JavaScript access
        //.secure(true)    // Use only over HTTPS
        .path("/")       // Available for all endpoints
        .maxAge(Duration.ofDays(7)) // Expiry time
        .sameSite("Strict") // Prevent CSRF attacks
        .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        AuthDTO responseBody = new AuthDTO(
            user.getUserId().toString(),
            user.getRole().toString()
        );

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(ApiResponse.success(responseBody));
    }

    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", "")
            .httpOnly(true)
            // .secure(true)
            .path("/")
            .maxAge(0) // Expire immediately
            .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(ApiResponse.success("Logged out"));
    }
}
