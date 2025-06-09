package dev.fredericoAkira.FtoA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping ("/register")
    public ResponseEntity<?> register(@RequestBody User request, HttpServletResponse response){
        return ResponseEntity.ok(authService.registerUser(request, response));
    }

    @PostMapping ("/login")
    public ResponseEntity<?> login (@RequestBody User request, HttpServletResponse response){
        return authService.authenticate(request, response);
    }

    @PostMapping ("/userLogout")
    public ResponseEntity<?> logout (HttpServletResponse response){
        return ResponseEntity.ok(authService.logout(response));
    }
}
