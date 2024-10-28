package com.example.FenceLink.tournament;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.leaderboard.Leaderboard;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentService tournamentService;

    @Test
    void addTournament_returnSavedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Summer Cup", "New York", LocalDate.of(2024, 11, 11), 
                "An exciting summer tournament", "Friendly", "foil", 
                "Male", "Teen", LocalDate.of(2025, 10, 11), LocalDate.of(2025, 11, 11), 100, null 
                );

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament savedTournament = tournamentService.addTournament(tournament);

        // Assert
        assertNotNull(savedTournament);
        verify(tournamentRepository).save(tournament);
    }

    @Test
    void getTournamentById_existingId_returnTournament() {
        // Arrange
        Leaderboard ldb = new Leaderboard();
        Tournament tournament = new Tournament("Summer Cup", "New York", LocalDate.of(2024, 11, 11), 
                "An exciting summer tournament", "Friendly", "foil", 
                "Male", "Teen", LocalDate.of(2025, 10, 11), LocalDate.of(2025, 11, 11), 100, null 
                );

        tournament.setId(1L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        // Act
        Tournament foundTournament = tournamentService.getTournamentById(1L);

        // Assert
        assertNotNull(foundTournament);
        assertEquals(1L, foundTournament.getId());
        verify(tournamentRepository).findById(1L);
    }

    @Test
    void getTournamentById_nonExistingId_throwException() {
        // Arrange
        Long id = 1L;
        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.getTournamentById(id);
        });

        String expectedMessage = "Tournament not found for id: " + id;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateTournament_existingId_returnUpdatedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Spring Cup", "Chicago", LocalDate.of(2024, 11, 11), 
                "Spring tournament in Chicago", "Knockout", "Professional", 
                "Male", "18+", LocalDate.of(2024, 11, 11), LocalDate.of(2024, 11, 11), 75, null);
        tournament.setId(1L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        // Act
        Tournament updatedTournament = tournamentService.updateTournament(tournament);

        // Assert
        assertNotNull(updatedTournament);
        assertEquals("Spring Cup", updatedTournament.getName());
        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void updateTournament_nonExistingId_throwException() {
        // Arrange
        Tournament tournament = new Tournament("Autumn Cup", "Miami", LocalDate.of(2024, 11, 11), 
                "Autumn tournament in Miami", "League", "Amateur", 
                "Female", "18+", LocalDate.of(2024, 11, 11), LocalDate.of(2024, 11, 11), 60, null);
        tournament.setId(99L);

        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.updateTournament(tournament);
        });

        String expectedMessage = "Tournament not found for id: " + tournament.getId();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void deleteTournament_existingId_success() {
        // Arrange
        Tournament tournament = new Tournament("Championship", "Boston", LocalDate.of(2024, 11, 11), 
                "Championship event in Boston", "Knockout", "Professional", 
                "Male", "18+", LocalDate.of(2024, 11, 11), LocalDate.of(2024, 11, 11), 100, null);
        tournament.setId(1L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        // Act
        tournamentService.deleteTournament(1L);

        // Assert
        verify(tournamentRepository).deleteById(1L);
    }

    @Test
    void deleteTournament_nonExistingId_throwException() {
        // Arrange
        Long id = 1L;
        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tournamentService.deleteTournament(id);
        });

        String expectedMessage = "Tournament not found for id: " + id;
        assertEquals(expectedMessage, exception.getMessage());
    }
}



