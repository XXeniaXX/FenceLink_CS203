package com.example.FenceLink.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository players;

    @Mock
    private TournamentRepository tournaments;

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
                .id((long) 123)
                .name("myName")
                .birthdate(LocalDate.parse("1999-09-09"))
                .bio("Live Laugh Love")
                .build();

        Player updatedPlayer = Player.builder()
                .id((long) 123)
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
        verify(players, times(2)).saveAndFlush(any(Player.class)); // Bc insert & edit details
    }

    @Test
    void addPlayer_updateBio_NotFound_ReturnException() {
        // Arrange
        Player player = Player.builder()
                .name("myName")
                .birthdate(LocalDate.parse("1999-09-09"))
                .bio("Live Laugh Love")
                .build();
        Long id = (long) 123;

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

    @Test
    void registerPlayerForTournament_alreadyRegistered() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player and tournament
        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setTournamentsRegistered(new ArrayList<>()); // Initialize the list

        // Simulate that the player is already registered for the tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        player.getTournamentsRegistered().add(tournament); // Add the tournament to the player's list

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act
        String result = playerService.registerPlayerForTournament(playerId, tournamentId);

        // Assert
        assertEquals("John Doe successfully registered for Spring Tournament.", result);
        assertEquals(1, player.getTournamentsRegistered().size()); // Ensure no duplicates added
        verify(players, times(0)).saveAndFlush(player); // No save should occur
    }

    @Test
    void registerPlayerForTournament_playerNotFound() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        when(players.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert
        String expectedMessage = "Player with ID " + playerId + " not found!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(players).findById(playerId);
    }

    @Test
    void registerPlayerForTournament_tournamentNotFound() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        Player player = Player.builder()
                .id(playerId)
                .name("John Doe")
                .tournamentsRegistered(new ArrayList<>())
                .build();

        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert
        String expectedMessage = "Tournament with ID " + tournamentId + " not found!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(tournaments).findById(tournamentId);
    }

    @Test
    void withdrawPlayerFromTournament_success() {
        // Arrange
        Long playerId = 1L;  //indictes number 1 is of type long
        Long tournamentId = 1L;

        // Create a player and tournament
        Player player = Player.builder()
                .id(playerId)
                .name("John Tan")
                .tournamentsRegistered(new ArrayList<>())
                .build();

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Summer Tournament");

        // Add tournament to player's registered tournaments
        player.getTournamentsRegistered().add(tournament);

        // Mock repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act
        String result = playerService.withdrawPlayerFromTournament(playerId, tournamentId);

        // Assert
        assertEquals("John Tan successfully withdrawn from Summer Tournament.", result);
        assertFalse(player.getTournamentsRegistered().contains(tournament)); // Ensure tournament was removed
        verify(players).save(player); // Ensure player is saved
    }

    @Test
    void withdrawPlayerFromTournament_playerNotFound() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        when(players.findById(playerId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.withdrawPlayerFromTournament(playerId, tournamentId);
        });

        // Assert
        String expectedMessage = "Player with ID " + playerId + " not found!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(players).findById(playerId);
    }

    @Test
    void withdrawPlayerFromTournament_tournamentNotFound() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        Player player = Player.builder()
                .id(playerId)
                .name("John Tan")
                .tournamentsRegistered(new ArrayList<>())
                .build();

        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.withdrawPlayerFromTournament(playerId, tournamentId);
        });

        // Assert
        String expectedMessage = "Tournament with ID " + tournamentId + " not found!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(tournaments).findById(tournamentId);
    }

    @Test
    void withdrawPlayerFromTournament_playerNotRegisteredForTournament() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Create player and tournament but not register the player
        Player player = Player.builder()
                .id(playerId)
                .name("John Tan")
                .tournamentsRegistered(new ArrayList<>()) // No tournaments registered
                .build();

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Summer Tournament");

        // Mock repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.withdrawPlayerFromTournament(playerId, tournamentId); // This should throw an exception
        });

        // Assert
        String expectedMessage = "John Tan is not registered for Summer Tournament.";
        String actualMessage = exception.getMessage(); // Get the message from the thrown exception
        assertTrue(actualMessage.contains(expectedMessage)); // Check if the actual message contains the expected message
        verify(players).findById(playerId); // Ensure that the findById method was called
    }

}