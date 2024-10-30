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
import java.util.Collections;
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

        Match match = new Match(null, 1, null, 200L, 300L, LocalDate.of(2023, 10, 30),
                                Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), 5, 3, 200L);
        match.setTournament(tournament); // Set tournament in match to establish relationship

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

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
        Tournament tournament = new Tournament();  // Create a tournament object with a specific ID
        tournament.setId(100L);

        Match match = new Match(null, 1, null, 200L, 300L, LocalDate.of(2023, 10, 30),
                                Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), 5, 3, 200L);
        match.setTournament(tournament);  // Set the tournament in the match

        when(tournamentRepository.findById(match.getTournament().getId())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(match);
        });

        assertEquals("Tournament not found for ID: " + match.getTournament().getId(), exception.getMessage());
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

        // Calculate expected matches based on dynamically generated pools
        int expectedMatches = calculateExpectedMatchesForPools(registeredPlayerIds, 7);

        // Verify that the exact number of matches were saved to the repository
        verify(matchRepository, times(expectedMatches)).save(any(Match.class));
    }

    // calculate expected matches for given pool sizes
    private int calculateExpectedMatchesForPools(List<Long> playerIds, int maxPoolSize) {
        List<List<Long>> pools = createPools(playerIds);
        int totalMatches = 0;
    
        for (List<Long> pool : pools) {
            int poolSize = pool.size();
            totalMatches += (poolSize * (poolSize - 1)) / 2; // Matches per pool in round-robin format
        }
        return totalMatches;
    }
    private List<List<Long>> createPools(List<Long> playerIds) {
        List<List<Long>> pools = new ArrayList<>();
        List<Long> shuffledPlayers = new ArrayList<>(playerIds);
        Collections.shuffle(shuffledPlayers);
    
        int totalPlayers = shuffledPlayers.size();
        int minPoolSize = 5;
        int maxPoolSize = 7;
    
        // Calculate the optimal number of pools
        int numPools = (int) Math.ceil((double) totalPlayers / maxPoolSize);
    
        // Ensure each pool can have at least the minimum pool size
        while (numPools * minPoolSize > totalPlayers) {
            numPools++;
        }
    
        int basePoolSize = totalPlayers / numPools;
        int extraPlayers = totalPlayers % numPools;
    
        int startIndex = 0;
        for (int i = 0; i < numPools; i++) {
            int currentPoolSize = basePoolSize + (i < extraPlayers ? 1 : 0); // Distribute extra players
            int endIndex = startIndex + currentPoolSize;
            pools.add(new ArrayList<>(shuffledPlayers.subList(startIndex, endIndex)));
            startIndex = endIndex;
        }
    
        return pools;
    }
}