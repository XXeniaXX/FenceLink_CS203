package com.example.FenceLink.match;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitialization() {
        assertNotNull(matchService);
        assertNotNull(tournamentRepository);
        assertNotNull(matchRepository);
    }

    @Test
    public void testCreateMatch() {
        // Arrange
        Tournament tournament = new Tournament("Summer Cup", "New York", LocalDate.of(2024, 11, 11),
                "An exciting summer tournament", "Friendly", "foil",
                "Male", "Teen", LocalDate.of(2025, 10, 11), LocalDate.of(2025, 11, 11), 100);
        tournament.setId(100L);

        Match match = new Match(null, 1, tournament.getId(), 200L, 300L, LocalDate.of(2023, 10, 30),
                                Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), 5, 3, 200L);

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(matchRepository.save(match)).thenReturn(match);

        // Act
        Match result = matchService.createMatch(match);

        // Assert
        assertEquals(match, result);
        verify(tournamentRepository, times(1)).findById(tournament.getId());
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    public void testCreateMatch_NullMatch() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(null);
        });

        assertEquals("Match cannot be null.", exception.getMessage());
    }

    @Test
    public void testCreateMatch_TournamentNotFound() {
        // Arrange
        Match match = new Match(null, 1, 100L, 200L, 300L, LocalDate.of(2023, 10, 30),
                                Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), 5, 3, 200L);

        when(tournamentRepository.findById(match.getTournamentId())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(match);
        });

        assertEquals("Tournament not found for ID: " + match.getTournamentId(), exception.getMessage());
    }


}