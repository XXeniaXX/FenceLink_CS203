package com.example.FenceLink.user;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // // Add new User
    // @PostMapping("/register")
    // @ResponseStatus(HttpStatus.CREATED)
    // public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) {
    //     User savedUser = userService.registerUser(userDTO);
    //     return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    // }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserDTO userDTO) {
        User savedUser = userService.registerUser(userDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("playerId", savedUser.getId()); // Include player ID in the response
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.userExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
