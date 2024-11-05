package com.example.FenceLink.match;

import com.example.FenceLink.player.PlayerService;
import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

    @Test
    public void testGetPlayerRank_WithIncompleteMatches() {
        // Arrange
        List<Match> matches = new ArrayList<>();
        matches.add(new Match(1L, 1, 1L, 101L, 102L, LocalDate.now(), null, null, 5, 3, null)); // Incomplete match

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            matchService.getPlayerRank(matches);
        });

        assertEquals("All matches need to be completed to calculate rankings.", exception.getMessage());
    }

    @Test
    public void testRankPlayer() {
        // Arrange
        List<Match> matches = new ArrayList<>();
        
        // Create some mock matches with valid winners
        matches.add(new Match(1L, 1, 1L, 101L, 102L, LocalDate.now(), null, null, 5, 3, 101L)); // Player 101 wins
        matches.add(new Match(2L, 1, 1L, 103L, 101L, LocalDate.now(), null, null, 4, 6, 101L)); // Player 101 wins again
        matches.add(new Match(3L, 1, 1L, 102L, 103L, LocalDate.now(), null, null, 3, 5, 103L)); // Player 103 wins

        // Log the match details for debugging
        System.out.println("Match details:");
        for (Match match : matches) {
            System.out.println("Match ID: " + match.getMatchId() + ", Winner: " + match.getWinner());
        }

        // Act
        List<Long> rankedPlayers = matchService.getPlayerRank(matches);

        // Assert
        assertEquals(3, rankedPlayers.size()); // Expecting 3 players in the ranking
        assertEquals(101L, rankedPlayers.get(0)); // Player 101 should be ranked first (2 wins)
        assertEquals(103L, rankedPlayers.get(1)); // Player 103 should be ranked second (1 win)
        assertEquals(102L, rankedPlayers.get(2)); // Player 102 should be ranked third (0 wins)

        // Optionally, log the rankings for debugging
        System.out.println("Ranked Players: " + rankedPlayers);
    }
    
    @Test
    public void testRankPlayerWithTies() {
        // Arrange
        List<Match> matches = new ArrayList<>();

        // Pool 1: Player 101, 102, 103
        matches.add(new Match(1L, 1, 1L, 101L, 102L, LocalDate.now(), null, null, 5, 3, 101L)); // Player 101 wins
        matches.add(new Match(2L, 1, 1L, 102L, 103L, LocalDate.now(), null, null, 4, 6, 103L)); // Player 103 wins
        matches.add(new Match(3L, 1, 1L, 103L, 101L, LocalDate.now(), null, null, 7, 5, 103L)); // Player 103 wins

        // Pool 2: Player 201, 202, 203
        matches.add(new Match(4L, 1, 1L, 201L, 202L, LocalDate.now(), null, null, 5, 4, 201L)); // Player 201 wins
        matches.add(new Match(5L, 1, 1L, 202L, 203L, LocalDate.now(), null, null, 3, 5, 203L)); // Player 203 wins
        matches.add(new Match(6L, 1, 1L, 203L, 201L, LocalDate.now(), null, null, 2, 6, 201L)); // Player 201 wins

        // Act
        List<Long> rankedPlayers = matchService.getPlayerRank(matches);

        // Assert
        assertEquals(6, rankedPlayers.size()); // There should be 6 players in total

        // Since players 103 and 201 both have 2 wins and 0 losses, their positions may be swapped randomly
        Long firstPlayer = rankedPlayers.get(0);
        Long secondPlayer = rankedPlayers.get(1);
        assertTrue((firstPlayer.equals(103L) && secondPlayer.equals(201L)) ||
                (firstPlayer.equals(201L) && secondPlayer.equals(103L)),
                "Players with equal wins and losses should be randomly ordered.");

        // Players 101 and 203 should follow, as both have 1 win and 1 loss
        Long thirdPlayer = rankedPlayers.get(2);
        Long fourthPlayer = rankedPlayers.get(3);
        assertTrue((thirdPlayer.equals(101L) && fourthPlayer.equals(203L)) ||
                (thirdPlayer.equals(203L) && fourthPlayer.equals(101L)),
                "Players with equal wins and losses should be randomly ordered.");

        // Players 102 and 202 should be last, both with 0 wins and 2 losses
        Long fifthPlayer = rankedPlayers.get(4);
        Long sixthPlayer = rankedPlayers.get(5);
        assertTrue((fifthPlayer.equals(102L) && sixthPlayer.equals(202L)) ||
                (fifthPlayer.equals(202L) && sixthPlayer.equals(102L)),
                "Players with equal losses should be randomly ordered.");

        // Log the rankings to review in the console
        System.out.println("Ranked Players with Ties: " + rankedPlayers);
    }

    @Test
    public void testUpdateMatchResults_Success() {
        // Arrange
        Long matchId = 1L;
        Match match = new Match();
        match.setMatchId(matchId);
        match.setPlayer1Id(101L);
        match.setPlayer2Id(102L);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Act
        matchService.updateMatchResults(matchId, 5, 3);

        // Assert
        assertEquals(5, match.getPlayer1points());
        assertEquals(3, match.getPlayer2points());
        assertEquals(101L, match.getWinner()); // Player 1 wins

        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(match);
    }

    @Test
    public void testUpdateMatchResults_MatchNotFound() {
        // Arrange
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.updateMatchResults(matchId, 5, 3);
        });

        assertEquals("Match not found for ID: " + matchId, exception.getMessage());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void testUpdateMatchResults_NegativePoints() {
        // Arrange
        Long matchId = 1L;
        Match match = new Match();
        match.setMatchId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.updateMatchResults(matchId, -1, 3);
        });

        assertEquals("Both players' points must be provided and cannot be negative.", exception.getMessage());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void testUpdateMatchResults_EqualPoints() {
        // Arrange
        Long matchId = 1L;
        Match match = new Match();
        match.setMatchId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.updateMatchResults(matchId, 4, 4);
        });

        assertEquals("Points cannot be equal. A winner must be determined.", exception.getMessage());
        verify(matchRepository, times(1)).findById(matchId);
    }

    @Test
    public void testGenerateSLP10() {
        // Arrange
        Tournament tournament = new Tournament();
        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) { // 10 players, so 6 will get a direct pass
            rankedPlayerIds.add(i);
        }

        // Act
        matchService.generateSLMatches(tournament, rankedPlayerIds);

        // Capture saved matches
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(8)).save(matchCaptor.capture()); // 2 matches should be saved

        List<Match> savedMatches = matchCaptor.getAllValues();
        List<Long> directPassPlayers = new ArrayList<>();
        List<String> matchups = new ArrayList<>();

        // Assert
        assertEquals(8, savedMatches.size(), "Total matches saved should be 8.");

        for (Match match : savedMatches) {
            assertNotNull(match.getTournament(), "Tournament should not be null.");
            assertEquals(2, match.getRoundNo(), "Round number should be 2.");

            if (match.getPlayer2Id() == 0L) {
                // Check for direct pass
                directPassPlayers.add(match.getPlayer1Id());
                assertEquals(match.getWinner(), match.getPlayer1Id(), "The winner of the direct pass match should be the real player.");
            } else {
                // Check for proper matchups
                matchups.add("Player " + match.getPlayer1Id() + " vs Player " + match.getPlayer2Id());
            }
        }

        // Verify that the top 6 players get a direct pass
        assertEquals(6, directPassPlayers.size(), "There should be 6 players getting a direct pass.");
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L, 6L), directPassPlayers, "The top 6 ranked players should get a direct pass.");

        // Verify the matchups of non-direct pass players
        assertEquals(2, matchups.size(), "There should be 2 matchups for non-direct pass players.");
        List<String> expectedMatchups = List.of(
            "Player 7 vs Player 10",
            "Player 8 vs Player 9"
        );
        assertEquals(expectedMatchups, matchups, "Matchups should be between the highest and lowest remaining ranked players.");

        // Optionally, print matchups for verification
        System.out.println("Matchups: " + matchups);
    }

    @Test
    public void testGenerateSLP15() {
        // Arrange
        Tournament tournament = new Tournament();
        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 15; i++) { // 15 players, so 1 will get a direct pass
            rankedPlayerIds.add(i);
        }
    
        // Act
        matchService.generateSLMatches(tournament, rankedPlayerIds);
    
        // Capture saved matches
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(8)).save(matchCaptor.capture()); // 8 matches should be saved
    
        List<Match> savedMatches = matchCaptor.getAllValues();
        List<Long> directPassPlayers = new ArrayList<>();
        List<String> matchups = new ArrayList<>();
    
        // Assert
        assertEquals(8, savedMatches.size(), "Total matches saved should be 8.");
    
        for (Match match : savedMatches) {
            assertNotNull(match.getTournament(), "Tournament should not be null.");
            assertEquals(2, match.getRoundNo(), "Round number should be 2.");
    
            if (match.getPlayer2Id() == 0L) {
                // Check for direct pass
                directPassPlayers.add(match.getPlayer1Id());
                assertEquals(match.getWinner(), match.getPlayer1Id(), "The winner of the direct pass match should be the real player.");
            } else {
                // Check for proper matchups
                matchups.add("Player " + match.getPlayer1Id() + " vs Player " + match.getPlayer2Id());
            }
        }
    
        // Verify that the top 1 player gets a direct pass
        assertEquals(1, directPassPlayers.size(), "There should be 1 player getting a direct pass.");
        assertEquals(List.of(1L), directPassPlayers, "The top ranked player should get a direct pass.");
    
        // Verify the matchups of non-direct pass players
        assertEquals(7, matchups.size(), "There should be 7 matchups for non-direct pass players.");
        List<String> expectedMatchups = List.of(
            "Player 2 vs Player 15",
            "Player 3 vs Player 14",
            "Player 4 vs Player 13",
            "Player 5 vs Player 12",
            "Player 6 vs Player 11",
            "Player 7 vs Player 10",
            "Player 8 vs Player 9"
        );
        assertEquals(expectedMatchups, matchups, "Matchups should be between the highest and lowest remaining ranked players.");
    
        // Optionally, print matchups for verification
        System.out.println("Matchups: " + matchups);
    }

    @Test
    public void testGenerateSLP18() {
        // Arrange
        Tournament tournament = new Tournament();
        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 18; i++) { // 18 players, so 14 will get a direct pass
            rankedPlayerIds.add(i);
        }

        // Act
        matchService.generateSLMatches(tournament, rankedPlayerIds);

        // Capture saved matches
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(16)).save(matchCaptor.capture()); // 16 matches should be saved

        List<Match> savedMatches = matchCaptor.getAllValues();
        List<Long> directPassPlayers = new ArrayList<>();
        List<String> matchups = new ArrayList<>();

        // Assert
        assertEquals(16, savedMatches.size(), "Total matches saved should be 16.");

        for (Match match : savedMatches) {
            assertNotNull(match.getTournament(), "Tournament should not be null.");
            assertEquals(2, match.getRoundNo(), "Round number should be 2.");

            if (match.getPlayer2Id() == 0L) {
                // Check for direct pass
                directPassPlayers.add(match.getPlayer1Id());
                assertEquals(match.getWinner(), match.getPlayer1Id(), "The winner of the direct pass match should be the real player.");
            } else {
                // Check for proper matchups
                matchups.add("Player " + match.getPlayer1Id() + " vs Player " + match.getPlayer2Id());
            }
        }

        // Verify that the top 14 players get a direct pass
        assertEquals(14, directPassPlayers.size(), "There should be 14 players getting a direct pass.");
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L), directPassPlayers, "The top 14 ranked players should get a direct pass.");

        // Verify the matchups of non-direct pass players
        assertEquals(2, matchups.size(), "There should be 2 matchups for non-direct pass players.");
        List<String> expectedMatchups = List.of(
            "Player 15 vs Player 18",
            "Player 16 vs Player 17"
        );
        assertEquals(expectedMatchups, matchups, "Matchups should be between the highest and lowest remaining ranked players.");

        // Optionally, print matchups for verification
        System.out.println("Matchups: " + matchups);
    }

    @Test
    public void testGenerateSLP30() {
        // Arrange
        Tournament tournament = new Tournament();
        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 30; i++) { // 30 players, so 2 will get a direct pass
            rankedPlayerIds.add(i);
        }

        // Act
        matchService.generateSLMatches(tournament, rankedPlayerIds);

        // Capture saved matches
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(16)).save(matchCaptor.capture()); // 16 matches should be saved

        List<Match> savedMatches = matchCaptor.getAllValues();
        List<Long> directPassPlayers = new ArrayList<>();
        List<String> matchups = new ArrayList<>();

        // Assert
        assertEquals(16, savedMatches.size(), "Total matches saved should be 16.");

        for (Match match : savedMatches) {
            assertNotNull(match.getTournament(), "Tournament should not be null.");
            assertEquals(2, match.getRoundNo(), "Round number should be 2.");

            if (match.getPlayer2Id() == 0L) {
                // Check for direct pass
                directPassPlayers.add(match.getPlayer1Id());
                assertEquals(match.getWinner(), match.getPlayer1Id(), "The winner of the direct pass match should be the real player.");
            } else {
                // Check for proper matchups
                matchups.add("Player " + match.getPlayer1Id() + " vs Player " + match.getPlayer2Id());
            }
        }

        // Verify that the top 2 players get a direct pass
        assertEquals(2, directPassPlayers.size(), "There should be 2 players getting a direct pass.");
        assertEquals(List.of(1L, 2L), directPassPlayers, "The top 2 ranked players should get a direct pass.");

        // Verify the matchups of non-direct pass players
        assertEquals(14, matchups.size(), "There should be 14 matchups for non-direct pass players.");
        List<String> expectedMatchups = List.of(
            "Player 3 vs Player 30",
            "Player 4 vs Player 29",
            "Player 5 vs Player 28",
            "Player 6 vs Player 27",
            "Player 7 vs Player 26",
            "Player 8 vs Player 25",
            "Player 9 vs Player 24",
            "Player 10 vs Player 23",
            "Player 11 vs Player 22",
            "Player 12 vs Player 21",
            "Player 13 vs Player 20",
            "Player 14 vs Player 19",
            "Player 15 vs Player 18",
            "Player 16 vs Player 17"
        );
        assertEquals(expectedMatchups, matchups, "Matchups should be between the highest and lowest remaining ranked players.");

        // Optionally, print matchups for verification
        System.out.println("Matchups: " + matchups);
    }
    




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