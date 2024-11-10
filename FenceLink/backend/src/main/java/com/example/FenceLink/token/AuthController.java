package com.example.FenceLink.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.FenceLink.user.*;

import java.util.HashMap;
import java.util.Map;

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
        String email = decodedJWT.getClaim("email").asString();

        // Fetch user details from the database
        User user = userService.findByEmail(email); // Use a service method to fetch the user

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

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

}