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
    
    //Unit testing for tournament registrtaion

    @Test
    void registerPlayerForTournament_successfulRegistration() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player
        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setBirthdate(LocalDate.of(2008, 1, 1)); // Example birthdate for a Youth
        player.setGender("Female");
        player.setTournamentsRegistered(new ArrayList<>()); // Initialize the list

        // Initialize the tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        // Set the registration date to a future date
        tournament.setRegistrationDate(LocalDate.of(2025, 11, 11));
        tournament.setAgeGroup("Youth"); // Set the tournament age group to Youth
        tournament.setGenderType("mixed"); // Set the tournament gender type
        tournament.setVacancy(1); // Ensure there is at least one vacancy

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act
        String result = playerService.registerPlayerForTournament(playerId, tournamentId);

        // Assert
        String expectedMessage = "John Doe successfully registered for Spring Tournament.";
        assertEquals(expectedMessage, result);
        assertEquals(1, player.getTournamentsRegistered().size()); // Check that the player is registered
        assertTrue(player.getTournamentsRegistered().contains(tournament)); // Ensure the tournament is in the player's list
        assertEquals(0, tournament.getVacancy()); // Check that the vacancy is reduced by 1
        verify(players).save(player); // Verify that the player was saved
        verify(tournaments).save(tournament); // Verify that the tournament was updated
    }
    
    @Test
    void registerPlayerForTournament_alreadyRegistered() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player
        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setBirthdate(LocalDate.of(2008, 1, 1)); // Example birthdate for a Youth
        player.setGender("Male");
        player.setTournamentsRegistered(new ArrayList<>()); // Initialize the list

        // Initialize the tournament
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        tournament.setRegistrationDate(LocalDate.of(2025, 11, 11));
        tournament.setAgeGroup("Youth"); // Set the tournament age group to Youth
        tournament.setGenderType("Male"); // Set the tournament gender type
        tournament.setVacancy(1); // Ensure there is at least one vacancy

        // Add the tournament to the player's registered tournaments to simulate that they're already registered
        player.getTournamentsRegistered().add(tournament); 

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert the expected message
        String expectedMessage = "Player already registered for the tournament!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that the save methods were not called since registration should not proceed
        verify(players, times(0)).save(player);
        verify(tournaments, times(0)).save(tournament);
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

        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setTournamentsRegistered(new ArrayList<>());

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
    void registerPlayerForTournament_registrationDeadlinePassed() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player
        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setBirthdate(LocalDate.of(2008, 1, 1));
        player.setGender("Male");
        player.setTournamentsRegistered(new ArrayList<>());

        // Initialize the tournament with a past registration date
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        tournament.setRegistrationDate(LocalDate.of(2023, 11, 11));
        tournament.setAgeGroup("Youth");
        tournament.setGenderType("Male");
        tournament.setVacancy(1);

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert the expected message
        String expectedMessage = "Registration deadline for Spring Tournament has passed!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerPlayerForTournament_noVacancies() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player
        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setBirthdate(LocalDate.of(2008, 1, 1));
        player.setGender("Male");
        player.setTournamentsRegistered(new ArrayList<>());

        // Initialize the tournament with no vacancies
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        tournament.setRegistrationDate(LocalDate.of(2025, 11, 11));
        tournament.setAgeGroup("Youth");
        tournament.setGenderType("Male");
        tournament.setVacancy(0); // No vacancies

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert the expected message
        String expectedMessage = "No vacancies left for Spring Tournament!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerPlayerForTournament_ageRequirementNotMet() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player as an adult
        Player player = new Player();
        player.setId(playerId);
        player.setName("John Doe");
        player.setBirthdate(LocalDate.of(1998, 1, 1)); // 23 years old
        player.setGender("Male");
        player.setTournamentsRegistered(new ArrayList<>());

        // Initialize the tournament with age group set to "Youth"
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        tournament.setRegistrationDate(LocalDate.of(2025, 11, 11));
        tournament.setAgeGroup("Youth"); // Set to Youth
        tournament.setGenderType("Male");
        tournament.setVacancy(1);

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert the expected message
        String expectedMessage = "Player's age does not meet the tournament's age requirement!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void registerPlayerForTournament_genderRequirementNotMet() {
        // Arrange
        Long playerId = 1L;
        Long tournamentId = 1L;

        // Initialize the player with a different gender
        Player player = new Player();
        player.setId(playerId);
        player.setName("Jane Doe");
        player.setBirthdate(LocalDate.of(2008, 1, 1));
        player.setGender("Female"); // Different gender
        player.setTournamentsRegistered(new ArrayList<>());

        // Initialize the tournament with a male gender requirement
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setName("Spring Tournament");
        tournament.setRegistrationDate(LocalDate.of(2025, 11, 11));
        tournament.setAgeGroup("Youth");
        tournament.setGenderType("Male"); // Male-only tournament
        tournament.setVacancy(1);

        // Mock the repository responses
        when(players.findById(playerId)).thenReturn(Optional.of(player));
        when(tournaments.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            playerService.registerPlayerForTournament(playerId, tournamentId);
        });

        // Assert the expected message
        String expectedMessage = "Player's gender does not match the tournament's gender requirement!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
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
    
    @Test
    void getRegisteredPlayerIds_returnsCorrectPlayerIds() {
        // Arrange: Create a sample tournament and players
        Tournament tournament = new Tournament();
        tournament.setId(1L);

        Player player1 = new Player();
        player1.setId(5L);
        Player player2 = new Player();
        player2.setId(10L);
        Player player3 = new Player();
        player3.setId(15L);

        // Add players to the tournament
        tournament.setPlayers(Arrays.asList(player1, player2, player3));

        // Mock the repository response
        when(tournaments.findById(anyLong())).thenReturn(Optional.of(tournament));

        // Act: Call the service method
        List<Long> playerIds = playerService.getRegisteredPlayerIds(1L);

        // Assert: Verify the response matches the expected player IDs
        assertEquals(Arrays.asList(5L, 10L, 15L), playerIds);
    }

    @Test
    void getRegisteredPlayerIds_throwsExceptionWhenTournamentNotFound() {
        // Arrange: Mock repository to return empty for non-existing tournament ID
        when(tournaments.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert: Verify that an exception is thrown if the tournament doesn't exist
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> playerService.getRegisteredPlayerIds(1L)
        );
        
        assertEquals("Tournament with ID 1 not found!", exception.getMessage());
    }
}