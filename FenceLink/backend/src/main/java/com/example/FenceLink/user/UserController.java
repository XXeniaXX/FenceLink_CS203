package com.example.FenceLink.user;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.FenceLink.token.CognitoJWTValidator;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    // Get all Users
    @GetMapping("/all")
    public List<User> getAllUsers() {
        System.out.println("all users");
        return userService.findAll();
    }

    //Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        String userName = user.getUsername();

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("user", user); 
        response.put("userName", userName);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserDTO userDTO) {
        User savedUser = userService.registerUser(userDTO);
        Long playerId = savedUser.getPlayer() != null ? savedUser.getPlayer().getId() : null;
        Long userId = savedUser.getId();
        String userRole = "player";// Default role is "player"

        //saved player and user information for frontend use
        Map<String, Object> response = new HashMap<>();
        response.put("playerId", playerId);
        response.put("userId", userId); 
        response.put("user", savedUser); 
        response.put("userRole", userRole);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDto) {
        try {
            userService.updateUser(id, userDto);
            
            //saved player and user information for frontend use
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("playerId", id); 
            response.put("userId", id);

            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            return new ResponseEntity<Map<String, Object>>(errorResponse, HttpStatus.BAD_REQUEST);
        }
}

    @PutMapping("/password/{id}")
    public ResponseEntity<Map<String, Object>> updateUserPassword(@PathVariable Long id, @RequestBody UserDTO userDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.updateUserPassword(id, userDto);
            response.put("message", "User password updated successfully");
            response.put("userId", id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

     // Add new Admin
     @PostMapping("/admins")
     @ResponseStatus(HttpStatus.CREATED)
     public ResponseEntity<User> addAdmin(@RequestBody UserDTO userDTO, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();
        
        if (CognitoJWTValidator.isAdmin(token)) {
            User savedUser = userService.createAdmin(userDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

     }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserWithPlayer(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "").trim();

        if(!CognitoJWTValidator.isAdmin(token)) {
            return new ResponseEntity<>("Access denied: Admin rights required", HttpStatus.FORBIDDEN);
        }

        if (!userService.userExists(id)) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        userService.deleteUserWithPlayer(id);
        return new ResponseEntity<>("User and associated Player deleted successfully", HttpStatus.OK);
    }
}
