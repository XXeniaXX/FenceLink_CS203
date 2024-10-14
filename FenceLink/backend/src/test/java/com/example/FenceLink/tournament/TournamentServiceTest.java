package com.example.FenceLink.tournament;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentService tournamentService;

    @Test
    void addTournament_returnSavedTournament() {
        // Arrange
        Tournament tournament = new Tournament("Summer Cup", "New York", new Date());

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
        Tournament tournament = new Tournament("Winter Cup", "Los Angeles", new Date());
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
        Tournament tournament = new Tournament("Spring Cup", "Chicago", new Date());
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
        Tournament tournament = new Tournament("Autumn Cup", "Miami", new Date());
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
        Tournament tournament = new Tournament("Championship", "Boston", new Date());
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

