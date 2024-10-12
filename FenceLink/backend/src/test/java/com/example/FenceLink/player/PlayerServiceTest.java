package com.example.FenceLink.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import java.time.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository players;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    void addPlayer_returnSavedPlayer() {
        // Arrange
        Player player = Player.builder()
                    .name("This is my new Name!")
                    .birthdate(LocalDate.parse("2001-01-01"))
                    .build();
    
        // Act
        Player savedPlayer = playerService.insertPlayer(player);

        // Assert
        assertNotNull(savedPlayer);
        verify(players).saveAndFlush(player);
    }

    @Test
    void addPlayer_birthdateFuture_ReturnException() {
        // Arrange
        Player player = Player.builder()
                        .name("myName")
                        .birthdate(LocalDate.parse("2025-05-05"))
                        .build();

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.insertPlayer(player);
        });
       
        String expectedMessage = "Birthdate cannot be in the future!";
        String actualMessage = exception.getMessage();

        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deletePlayerSuccess() {
         // Arrange
        Player player = Player.builder()
                        .name("MyName")
                        .birthdate(LocalDate.parse("1999-09-09"))
                        .build();

        // Simulate the player exists
        playerService.insertPlayer(player);
        when(players.findById(player.getId())).thenReturn(Optional.of(player));

        // Act
        playerService.deletePlayerById(player.getId());

        // Assert
        verify(players, times(1)).deletePlayerById(player.getId());
    }
    
    @Test
    void addPlayer_updateBio_Success() {
        // Arrange
        Player player = Player.builder()
                        .id((long)123)
                        .name("myName")
                        .birthdate(LocalDate.parse("1999-09-09"))
                        .bio("Live Laugh Love")
                        .build();
                        
        Player updatedPlayer = Player.builder()
                                .id((long)123)
                                .name("myName")
                                .birthdate(LocalDate.parse("1999-09-09"))
                                .bio(":(")
                                .build();

    // Simulate the player exists
    playerService.insertPlayer(player);
    when(players.findById(player.getId())).thenReturn(Optional.of(player));

    // Act
    Player result = playerService.updatePlayer(player.getId(), updatedPlayer);

    // Assert
    assertEquals(":(", result.getBio());
    verify(players, times(2)).saveAndFlush(any(Player.class));  // Bc insert & edit details
    }

    @Test
    void addPlayer_updateBio_NotFound_ReturnException() {
        // Arrange
        Player player = Player.builder()
                        .name("myName")
                        .birthdate(LocalDate.parse("1999-09-09"))
                        .bio("Live Laugh Love")
                        .build();
        Long id = (long)123;

        when(players.findById(id)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.updatePlayer(id, player);
        });
       
        String expectedMessage = "Player with ID: " + id + " not found!";
        String actualMessage = exception.getMessage();

        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
        verify(players).findById(id);
    }
}