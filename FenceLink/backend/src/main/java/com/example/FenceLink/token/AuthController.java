package com.example.FenceLink.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.FenceLink.user.*;

import java.util.*;
;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserAuthService userAuthService;
    
    @Autowired
    private UserService userService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDTO loginDTO) {
        String token = userAuthService.loginUser(loginDTO);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Retrieve user details after successful login
        User user = userService.findByEmail(loginDTO.getEmail()); 
        Long userId = user.getId();

        // Create a response map
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> requestBody) {
        String jwtToken = requestBody.get("token");

        if (jwtToken == null || jwtToken.isEmpty()) {

            // Return a JSON response for missing token
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token is missing in the request body");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        String validationMessage = CognitoJWTValidator.validateToken(jwtToken);
        if (!"Token is valid".equals(validationMessage)) {

            // Return a JSON response for invalid token
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid token");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        List<String> roles = decodedJWT.getClaim("cognito:groups").asList(String.class);
        String userRole = roles != null && roles.contains("admin") ? "admin" : "player";
        String email = decodedJWT.getClaim("email").asString();

        User user = userService.findByEmail(email); 
        Long playerId = user.getPlayer() != null ? user.getPlayer().getId() : null;
        

        if (user == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Return a JSON response with user details and validation message
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "Token is valid");
        successResponse.put("userId", user.getId());
        successResponse.put("username", user.getUsername());
        successResponse.put("playerId", playerId);
        successResponse.put("userRole", userRole);

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

}