package com.example.FenceLink.match;

import com.example.FenceLink.player.PlayerService;
import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerServiceImpl playerService; // Use PlayerService, not PlayerServiceImpl

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Ensure mocks are initialized
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

    //Test match system
    @Test
    public void testGenerateMatches() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStartDate(LocalDate.of(2025, 10, 10));
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Mock list of registered player IDs
        List<Long> registeredPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) { // Assuming 10 players
            registeredPlayerIds.add(i);
        }
        
        when(playerService.getRegisteredPlayerIds(tournamentId)).thenReturn(registeredPlayerIds);

        // Act
        matchService.generateMatches(tournamentId);

        // Verify pools were created and players matched within pools
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(playerService, times(1)).getRegisteredPlayerIds(tournamentId);

        // Calculate expected matches within randomized pools
        int poolSize = registeredPlayerIds.size() <= 15 ? 5 : 7;
        int expectedMatchesPerPool = (poolSize * (poolSize - 1)) / 2;
        int totalPools = (int) Math.ceil((double) registeredPlayerIds.size() / poolSize);
        int expectedTotalMatches = expectedMatchesPerPool * totalPools;

        // Verify that the expected number of matches is saved
        verify(matchRepository, times(expectedTotalMatches)).save(any(Match.class));
    }

    @Test
    public void testGenerate_ThrowsException_MinimumPlayer() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStartDate(LocalDate.of(2025, 10, 10));
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
    
        // Mock a list of 9 registered player IDs (below the minimum requirement of 10)
        List<Long> registeredPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 9; i++) {
            registeredPlayerIds.add(i);
        }
    
        // Mock the player service to return the list of player IDs
        when(playerService.getRegisteredPlayerIds(tournamentId)).thenReturn(registeredPlayerIds);
    
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            matchService.generateMatches(tournamentId);
        });
    
        // Check that the exception message is correct
        String expectedMessage = "Tournament requires a minimum of 10 players";
        assertEquals(expectedMessage, exception.getMessage());
    
        // Verify that the repository interactions didn't proceed past this check
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(playerService, times(1)).getRegisteredPlayerIds(tournamentId);
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    public void testGenerateMatchesWith33Players() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setStartDate(LocalDate.of(2025, 10, 10));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // Mock list of 33 registered player IDs
        List<Long> registeredPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 33; i++) {
            registeredPlayerIds.add(i);
        }

        // Mock the player service to return the list of player IDs
        when(playerService.getRegisteredPlayerIds(tournamentId)).thenReturn(registeredPlayerIds);

        // Act
        matchService.generateMatches(tournamentId);

        // Assert
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(playerService, times(1)).getRegisteredPlayerIds(tournamentId);

        // Expected match count:
        // Each player in a pool plays with every other player in the same pool.
        // Calculate the expected number of matches based on pool distribution:
        int expectedMatches = calculateExpectedMatchesForPools(registeredPlayerIds, 7); // Assuming 7-player pools

        // Verify the exact number of matches were saved to the repository
        verify(matchRepository, times(expectedMatches)).save(any(Match.class));
    }

    // calculate expected matches for given pool sizes
    private int calculateExpectedMatchesForPools(List<Long> playerIds, int poolSize) {
        int totalMatches = 0;
        int totalPlayers = playerIds.size();

        // Calculate match counts per pool
        for (int i = 0; i < totalPlayers; i += poolSize) {
            int end = Math.min(i + poolSize, totalPlayers);
            int playersInPool = end - i;
            totalMatches += (playersInPool * (playersInPool - 1)) / 2; // Round-robin within the pool
        }

        return totalMatches;
    }
}