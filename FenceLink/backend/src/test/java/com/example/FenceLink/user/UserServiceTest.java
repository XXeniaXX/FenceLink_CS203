package com.example.FenceLink.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.FenceLink.player.Player;
import com.example.FenceLink.player.PlayerRepository;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PlayerRepository players;

    @Mock
    private UserRepository users;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_NewUser_ReturnSavedUser() {
        // Arrange ***
        UserDTO userDto = new UserDTO("newuser", "newuser@example.com", "password123");
        User newUser = new User(1L, "newuser", "password123", "newuser@example.com", "ROLE_PLAYER", new Player());

        when(users.findByEmail(any(String.class))).thenReturn(Optional.empty());

        when(users.save(any(User.class))).thenReturn(newUser);

        // Act ***
        User savedUser = userService.registerUser(userDto);

        // Assert ***
        assertNotNull(savedUser); 
        assertEquals("newuser", savedUser.getUsername()); 
        assertEquals("newuser@example.com", savedUser.getEmail()); 
        assertEquals("ROLE_PLAYER", savedUser.getRole()); 

        verify(users).findByEmail(userDto.getEmail());
        verify(users).save(any(User.class));
    }


    @Test
    void registerUser_PlayerCreatedAndLinkedToUser() {
        // Arrange ***
        UserDTO userDto = new UserDTO("testuser", "testuser@example.com", "password123");
        
        Player newPlayer = new Player(null, "testuser", null, null, null, null, null, null, 0, new ArrayList<>(), null);
        User newUser = new User(1L, "testuser", "password123", "testuser@example.com", "ROLE_PLAYER", newPlayer);
        
        newPlayer.setUser(newUser);

        when(users.findByEmail(any(String.class))).thenReturn(Optional.empty());

        when(users.save(any(User.class))).thenReturn(newUser);

        // Act ***
        User savedUser = userService.registerUser(userDto);

        // Assert ***
        assertNotNull(savedUser); 
        assertNotNull(savedUser.getPlayer()); 
        assertEquals("testuser", savedUser.getPlayer().getName()); 
        assertEquals(savedUser, savedUser.getPlayer().getUser());

        // Verify that the repository methods were called
        verify(users).findByEmail(userDto.getEmail());
        verify(users).save(any(User.class));
    }

    @Test
    void createAdmin_NewAdmin_ReturnSavedAdmin() {
        // Arrange ***
        UserDTO userDto = new UserDTO("adminuser", "admin@example.com", "adminpass123");
        User newAdmin = new User(1L, "adminuser", "adminpass123", "admin@example.com", "ROLE_ADMIN", null);

        when(users.findByEmail(any(String.class))).thenReturn(Optional.empty());

        when(users.save(any(User.class))).thenReturn(newAdmin);

        // Act ***
        User savedAdmin = userService.createAdmin(userDto);

        // Assert ***
        assertNotNull(savedAdmin); 
        assertEquals("adminuser", savedAdmin.getUsername()); 
        assertEquals("admin@example.com", savedAdmin.getEmail()); 
        assertEquals("ROLE_ADMIN", savedAdmin.getRole()); 

        verify(users).findByEmail(userDto.getEmail());
        verify(users).save(any(User.class));
    }

    @Test
    void findById_UserExists_ReturnUser() {
        // Arrange ***
        Long userId = 1L;
        User existingUser = new User(userId, "testuser", "password123", "testuser@example.com", "ROLE_PLAYER", null);

        when(users.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act ***
        User foundUser = userService.findById(userId);

        // Assert ***
        assertNotNull(foundUser); 
        assertEquals(userId, foundUser.getId()); 
        assertEquals("testuser", foundUser.getUsername()); 

        verify(users).findById(userId);
    }

    @Test
    void findById_UserNotExists_ThrowsException() {
        // Arrange ***
        Long userId = 1L;

        when(users.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert ***
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found!", exception.getMessage()); 

        verify(users).findById(userId);
    }


    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Arrange ***
        UserDTO userDto = new UserDTO("existinguser", "existinguser@example.com", "password123");

        when(users.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));

        // Act & Assert ***
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userDto);
        });

        assertEquals("Email is already in use!", exception.getMessage()); 

        verify(users).findByEmail(userDto.getEmail());
        verify(users, times(0)).save(any(User.class)); 
    }

    @Test
    void createAdmin_EmailAlreadyExists_ThrowsException() {
        // Arrange ***
        UserDTO userDto = new UserDTO("adminuser", "admin@example.com", "adminpass123");

        when(users.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));

        // Act & Assert ***
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createAdmin(userDto);
        });

        assertEquals("Email is already in use!", exception.getMessage()); 

        verify(users).findByEmail(userDto.getEmail());
        verify(users, times(0)).save(any(User.class));
    }

    @Test
    void deleteUserById_UserExists_SuccessfullyDeleted() {
        // Arrange ***
        Long userId = 1L;

        when(users.findById(userId)).thenReturn(Optional.of(new User()));

        // Act ***
        userService.deleteUserById(userId);

        // Assert ***
        verify(users).deleteById(userId);
    }

    @Test
    void deleteUserById_UserNotExists_ThrowsException() {
        // Arrange ***
        Long userId = 1L;

        when(users.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert ***
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUserById(userId);
        });

        assertEquals("User not found!", exception.getMessage()); 

        verify(users, times(0)).deleteById(userId);
    }


    
}
