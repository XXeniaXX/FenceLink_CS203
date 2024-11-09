package com.example.FenceLink.user;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.FenceLink.player.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    //private BCryptPasswordEncoder passwordEncoder;

    public void checkUser(UserDTO userDto) throws IllegalArgumentException {
       
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty!");
        }

        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }

        if (userDto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be more than 8 characters!");
        }
    
    }

    public void checkPassword(UserDTO userDto) throws IllegalArgumentException {
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty!");
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    public boolean userExists(Long id) throws IllegalArgumentException {
        return userRepository.findById(id).isPresent();
    }

    public User registerUser(UserDTO userDto) throws IllegalArgumentException {
        checkUser(userDto); 

        //String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        String hashedPassword = userDto.getPassword();
        
        //check if email already exists before saving
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        String role = "ROLE_PLAYER";

        Player newPlayer = new Player(null, userDto.getUsername(), null, null, null, null, null, null, 0, new ArrayList<>(), null);

        User newUser = new User(null, userDto.getUsername(), userDto.getEmail(), hashedPassword, role, newPlayer);

        newPlayer.setUser(newUser);

        return userRepository.save(newUser);
    }


    public User createAdmin(UserDTO userDto) throws IllegalArgumentException {
        checkUser(userDto); 

        //String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        String hashedPassword = userDto.getPassword();
        
        
        //check if email already exists before saving
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        String role = "ROLE_ADMIN";

        User newAdmin = new User(null, userDto.getUsername(), userDto.getEmail(), hashedPassword, role, null);

        return userRepository.save(newAdmin);
    }

    public User updateUser(Long id, UserDTO userDto) throws IllegalArgumentException {
        User existingUser = findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found!");
        }

        // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // String username = null;

        // if (principal instanceof UserDetails) {
        //     username = ((UserDetails) principal).getUsername();
        // } else {
        //     username = principal.toString();
        // }

        // // Ensure the authenticated user is authorized to update the user
        // if (!existingUser.getUsername().equals(username)) {
        //     throw new AccessDeniedException("Unauthorized to update this user!");
        // }

        checkUser(userDto);

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());

        return userRepository.save(existingUser);
    }

    public User updateUserPassword(Long id, UserDTO userDto) throws IllegalArgumentException {
        User existingUser = findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found!");
        }

        checkPassword(userDto);

        existingUser.setPassword(userDto.getPassword());

        return userRepository.save(existingUser);
    }

    @Override
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUserById(Long id) throws IllegalArgumentException {
        if (!userExists(id)) {
            throw new IllegalArgumentException("User not found!");
        }
        userRepository.deleteById(id);
    }

}
