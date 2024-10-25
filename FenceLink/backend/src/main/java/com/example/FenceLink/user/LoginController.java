package com.example.FenceLink.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        // Token verification happens automatically by Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Token is valid
            return ResponseEntity.ok("Login successful!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed!");
        }
    }
}
