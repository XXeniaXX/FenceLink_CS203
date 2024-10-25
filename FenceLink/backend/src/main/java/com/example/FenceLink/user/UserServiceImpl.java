package com.example.FenceLink.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.FenceLink.player.Player;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

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

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    public boolean userExists(Long id) throws IllegalArgumentException {
        return userRepository.findById(id).isPresent();
    }

    public User registerUser(UserDTO userDto) throws IllegalArgumentException {
        checkUser(userDto); 

        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        
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

        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        
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

        checkUser(userDto);

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());

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
