package com.example.FenceLink.user;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;

import com.example.FenceLink.token.CognitoJWTValidator;

import java.io.IOException;
import java.net.MalformedURLException;


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
        String userRole = "player";

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
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("playerId", id); // Include the player ID

            // Return ResponseEntity with the type explicitly stated
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            // Return ResponseEntity with the type explicitly stated
            return new ResponseEntity<Map<String, Object>>(errorResponse, HttpStatus.BAD_REQUEST);
        }
}

    @PutMapping("/updatepassword/{id}")
    public ResponseEntity<String> updateUserPassword(@PathVariable Long id, @RequestBody UserDTO userDto) {
        try {
            userService.updateUserPassword(id, userDto);
            return new ResponseEntity<>("User pasword updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

     // Add new Admin
     @PostMapping("/createadmin")
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

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    //     if (!userService.userExists(id)) {
    //         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //     }
    //     userService.deleteUserById(id);
    //     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }
    
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
