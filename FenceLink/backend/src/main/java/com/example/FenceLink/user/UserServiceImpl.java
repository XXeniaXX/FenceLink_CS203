package com.example.FenceLink.user;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.FenceLink.security.PasswordUtil;
import org.springframework.stereotype.Service;
import com.example.FenceLink.player.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerServiceImpl playerService;

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

        String hashedPassword = PasswordUtil.encodePassword(userDto.getPassword());
        
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        String role = "ROLE_PLAYER"; // Default role for new users

        Player newPlayer = new Player(null, userDto.getUsername(), null, null, null, null, null, null, 100, new ArrayList<>(), null);

        User newUser = new User(null, userDto.getUsername(), userDto.getEmail(), hashedPassword, role, newPlayer);

        newPlayer.setUser(newUser);

        return userRepository.save(newUser);
    }


    public User createAdmin(UserDTO userDto) throws IllegalArgumentException {
        checkUser(userDto); 

        String hashedPassword = PasswordUtil.encodePassword(userDto.getPassword());        
        
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
    public void deleteUserById(Long id) throws IllegalArgumentException {
        if (!userExists(id)) {
            throw new IllegalArgumentException("User not found!");
        }
        userRepository.deleteById(id);
    }

    public void deleteUserWithPlayer(Long userId) {
        User user = findById(userId);
        if (user != null) {
            // Check if this user has an associated player
            Player associatedPlayer = user.getPlayer();
            if (associatedPlayer != null) {
                playerService.deletePlayerById(associatedPlayer.getId()); 
            }
            userRepository.deleteById(userId); 
        }
    }
    
}
