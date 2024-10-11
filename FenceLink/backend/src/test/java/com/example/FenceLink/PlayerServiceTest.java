package com.example.FenceLink;

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

import com.example.FenceLink.player.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository players;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void addPlayer_newName_returnSavedPlayer() {
        // Arrange
        Player player = Player.builder()
                    .id("514")
                    .name("This is my new Name!")
                    .birthdate(LocalDate.parse("2001-01-01"))
                    .build();

        // Mock the save operation
        when(players.save(any(Player.class))).thenReturn(player);
    
        // Act
        Player savedPlayer = playerService.addPlayer(player);

        // Assert
        assertNotNull(savedPlayer);
        verify(players).findById(player.getId());
        verify(players).save(player);
    }

    @Test
    void updatePlayerBio_NotFound_ReturnException() {
        // Arrange
        Player player = Player.builder()
                        .id("myId")
                        .name("myName")
                        .birthdate(LocalDate.parse("1999-09-09"))
                        .bio("Live Laugh Love")
                        .build();
        String id = "nonexistent";

        when(players.findById(id)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.updatePlayer(id, player);
        });
       
        String expectedMessage = "Player not found!";
        String actualMessage = exception.getMessage();

        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
        verify(players).findById(id);
    }

    @Test
    void addPlayer_SameId_ReturnException() {
        // Arrange
        Player p1 = Player.builder()
                    .id("11")
                    .name("Ethan")
                    .birthdate(LocalDate.parse("1999-09-09"))
                    .build();

        Player p2 = Player.builder()
                    .id("11")
                    .name("Mia")
                    .birthdate(LocalDate.parse("2001-01-01"))
                    .build();

        // Act    
        playerService.addPlayer(p1);
        when(players.findById("11")).thenReturn(Optional.of(p1));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.addPlayer(p2);
        });

        String expectedMessage = "Player with same ID already exists!";
        String actualMessage = exception.getMessage();

        // Assert
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addPlayer_birthdateFuture_ReturnException() {
        // Arrange
        Player player = Player.builder()
                        .id("myId")
                        .name("myName")
                        .birthdate(LocalDate.parse("2025-05-05"))
                        .build();

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.addPlayer(player);
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
                        .id("1")
                        .name("MyName")
                        .birthdate(LocalDate.parse("1999-09-09"))
                        .build();

        // Simulate the player exists
        playerService.addPlayer(player);
        when(players.findById("1")).thenReturn(Optional.of(player));

        // Act
        playerService.deletePlayer(player.getId());

        // Assert
        verify(players, times(1)).deleteById(player.getId());
    }

    @Test
    void editPlayerDetails_Success() {
        Player existingPlayer = Player.builder()
                                .name("Susie")
                                .id("1")
                                .birthdate(LocalDate.parse("1999-09-09"))
                                .build();

        Player updatedPlayer = Player.builder()
                                .name("New Susie")
                                .id("1")
                                .birthdate(LocalDate.parse("1999-09-09"))
                                .build();

        
    when(players.findById(existingPlayer.getId())).thenReturn(Optional.of(existingPlayer));
    when(players.save(any(Player.class))).thenReturn(updatedPlayer);

    // Act
    Player result = playerService.editPlayerDetails(existingPlayer.getId(), updatedPlayer);

    // Assert
    assertEquals("New Susie", result.getName());
    verify(players, times(1)).save(any(Player.class));
    }
}