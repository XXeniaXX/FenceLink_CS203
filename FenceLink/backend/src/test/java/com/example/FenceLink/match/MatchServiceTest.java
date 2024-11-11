package com.example.FenceLink.match;

import com.example.FenceLink.MatchRank.MatchRankService;
import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;
import com.example.FenceLink.MatchRank.MatchRank;
import com.example.FenceLink.MatchRank.MatchRankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;
    

    @Mock
    private MatchRankService matchRankService;

    @Mock
    private MatchRankRepository matchRankRepository;

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
    public void testUpdateMatchResults_Success() {
        // Arrange
        Long matchId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(1L); // Set the tournament ID

        Match match = new Match();
        match.setMatchId(matchId);
        match.setTournament(tournament); // Set the tournament
        match.setPlayer1Id(101L);
        match.setPlayer2Id(102L);

        MatchRank player1Rank = new MatchRank(1L, tournament, 101L, 0, 0, 0, false);
        MatchRank player2Rank = new MatchRank(2L, tournament, 102L, 0, 0, 0, false);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRankRepository.findByTournamentIdAndPlayerId(1L, 101L))
            .thenReturn(Optional.of(player1Rank));
        when(matchRankRepository.findByTournamentIdAndPlayerId(1L, 102L))
            .thenReturn(Optional.of(player2Rank));

        // Act
        matchService.updateMatchResults(matchId, 5, 3);

        // Assert
        assertEquals(5, match.getPlayer1points());
        assertEquals(3, match.getPlayer2points());
        assertEquals(101L, match.getWinner()); // Player 1 wins

        // Check if the win and loss counts are updated
        assertEquals(1, player1Rank.getWinCount()); // Player 1 should have 1 win
        assertEquals(0, player1Rank.getLossCount());
        assertEquals(0, player2Rank.getWinCount());
        assertEquals(1, player2Rank.getLossCount()); // Player 2 should have 1 loss

        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(match);
        verify(matchRankRepository, times(1)).save(player1Rank);
        verify(matchRankRepository, times(1)).save(player2Rank);
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
    public void testUpdateMatchResults_Round1_NoElimination() {
        // Arrange
        Long matchId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(1L); // Set the tournament ID

        Match match = new Match();
        match.setMatchId(matchId);
        match.setTournament(tournament); // Set the tournament
        match.setPlayer1Id(101L);
        match.setPlayer2Id(102L);
        match.setRoundNo(1); // Set the round number to 1 (first round)

        MatchRank player1Rank = new MatchRank(1L, tournament, 101L, 0, 0, 0, false);
        MatchRank player2Rank = new MatchRank(2L, tournament, 102L, 0, 0, 0, false);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchRepository.countByTournamentIdAndRoundNo(1L, 1)).thenReturn(4L); // Not a semi-final (more than 2 matches)
        when(matchRankRepository.findByTournamentIdAndPlayerId(1L, 101L))
            .thenReturn(Optional.of(player1Rank));
        when(matchRankRepository.findByTournamentIdAndPlayerId(1L, 102L))
            .thenReturn(Optional.of(player2Rank));

        // Act
        matchService.updateMatchResults(matchId, 5, 3);

        // Assert
        assertEquals(5, match.getPlayer1points());
        assertEquals(3, match.getPlayer2points());
        assertEquals(101L, match.getWinner()); // Player 1 wins

        // Check if the win and loss counts are updated correctly without elimination
        assertEquals(1, player1Rank.getWinCount());
        assertEquals(0, player1Rank.getLossCount());
        assertFalse(player1Rank.isEliminated());
        assertEquals(0, player2Rank.getWinCount());
        assertEquals(1, player2Rank.getLossCount());
        assertFalse(player2Rank.isEliminated()); // Player 2 should not be eliminated

        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(match);
        verify(matchRankRepository, times(1)).save(player1Rank);
        verify(matchRankRepository, times(1)).save(player2Rank);
    }
   
    @Test
    public void testGenerateSLP10() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setId(1L);

        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            rankedPlayerIds.add(i);
        }
        // Mock the rank-related methods in matchRankService
        when(matchRankService.getRankedPlayerIds(tournament.getId())).thenReturn(rankedPlayerIds);
        doNothing().when(matchRankService).updateCurrentRank(tournament.getId());
         // Mock the MatchRank objects to verify win count increment
        List<MatchRank> matchRanks = rankedPlayerIds.stream()
            .map(id -> {
                MatchRank matchRank = new MatchRank();
                matchRank.setPlayerId(id);
                matchRank.setWinCount(0); // Initially set win count to 0
                matchRank.setEliminated(false);
                return matchRank;
            })
            .collect(Collectors.toList());
        
        for (MatchRank matchRank : matchRanks) {
            when(matchRankRepository.findByTournamentIdAndPlayerId(tournament.getId(), matchRank.getPlayerId()))
                .thenReturn(Optional.of(matchRank));
        }
        
        // Act
        matchService.generateSLMatches(tournament);

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
                 // Verify win count increment
                MatchRank matchRank = matchRanks.stream()
                    .filter(mr -> mr.getPlayerId().equals(match.getPlayer1Id()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("MatchRank not found for player ID: " + match.getPlayer1Id()));
                assertEquals(1, matchRank.getWinCount(), "Win count should be incremented by 1 for direct pass player.");
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
    public void testGenerateSLP30() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 30; i++) { // 30 players, so 2 will get a direct pass
            rankedPlayerIds.add(i);
        }
        when(matchRankService.getRankedPlayerIds(tournament.getId())).thenReturn(rankedPlayerIds);
        doNothing().when(matchRankService).updateCurrentRank(tournament.getId());
        // Mock the MatchRank objects to verify win count increment
        List<MatchRank> matchRanks = rankedPlayerIds.stream()
            .map(id -> {
                MatchRank matchRank = new MatchRank();
                matchRank.setPlayerId(id);
                matchRank.setWinCount(0); // Initially set win count to 0
                matchRank.setEliminated(false);
                return matchRank;
            })
            .collect(Collectors.toList());
        
        for (MatchRank matchRank : matchRanks) {
            when(matchRankRepository.findByTournamentIdAndPlayerId(tournament.getId(), matchRank.getPlayerId()))
                .thenReturn(Optional.of(matchRank));
        }

        // Act
        matchService.generateSLMatches(tournament);

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
                // Verify win count increment
                MatchRank matchRank = matchRanks.stream()
                    .filter(mr -> mr.getPlayerId().equals(match.getPlayer1Id()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("MatchRank not found for player ID: " + match.getPlayer1Id()));
                assertEquals(1, matchRank.getWinCount(), "Win count should be incremented by 1 for direct pass player.");
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
    
    @Test
    public void testGenerateDEMatchesFor8Players() {
        // Arrange
        Tournament mockTournament = new Tournament();
        mockTournament.setId(1L);

        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 8; i++) {
            rankedPlayerIds.add(i);
        }

        when(matchRankService.getRankedPlayerIds(1L)).thenReturn(rankedPlayerIds);

        // Act
        matchService.generateDEMatches(mockTournament);

        // Assert
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(4)).save(matchCaptor.capture());
        List<Match> savedMatches = matchCaptor.getAllValues();

        // Example assertions for 8 players
        assertEquals(4, savedMatches.size()); // Ensure we have 4 matches

        // Match 1
        assertEquals(1L, savedMatches.get(0).getPlayer1Id().longValue());
        assertEquals(8L, savedMatches.get(0).getPlayer2Id().longValue());
        
        // Match 2
        assertEquals(3L, savedMatches.get(1).getPlayer1Id().longValue());
        assertEquals(6L, savedMatches.get(1).getPlayer2Id().longValue());
        
        // Match 3
        assertEquals(4L, savedMatches.get(2).getPlayer1Id().longValue());
        assertEquals(5L, savedMatches.get(2).getPlayer2Id().longValue());
        
        // Match 4
        assertEquals(2L, savedMatches.get(3).getPlayer1Id().longValue());
        assertEquals(7L, savedMatches.get(3).getPlayer2Id().longValue());
    }

    @Test
    public void testGenerateDEMatchesFor17Players() {
        // Arrange
        Tournament mockTournament = new Tournament();
        mockTournament.setId(1L);

        List<Long> rankedPlayerIds = new ArrayList<>();
        for (long i = 1; i <= 17; i++) { // Add 17 players
            rankedPlayerIds.add(i);
        }

        when(matchRankService.getRankedPlayerIds(1L)).thenReturn(rankedPlayerIds);

        // Mock behavior for excluded player
        Long excludedPlayerId = 17L; // Last player to be removed
        MatchRank excludedPlayerRank = new MatchRank();
        excludedPlayerRank.setPlayerId(excludedPlayerId);
        excludedPlayerRank.setEliminated(false);

        when(matchRankRepository.findByTournamentIdAndPlayerId(1L, excludedPlayerId))
            .thenReturn(Optional.of(excludedPlayerRank));

        // Act
        matchService.generateDEMatches(mockTournament);

        // Assert
        // Verify the last player is excluded
        assertTrue(excludedPlayerRank.isEliminated());
        verify(matchRankRepository).save(excludedPlayerRank);

        // Capture matches that were saved
        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository, times(8)).save(matchCaptor.capture());
        List<Match> savedMatches = matchCaptor.getAllValues();

        // Verify that the matches were generated correctly
        assertEquals(8, savedMatches.size()); // Ensure we have 8 matches since one player was removed

        // Check that the matches are generated for the remaining 16 players
        // Match 1
        assertEquals(1L, savedMatches.get(0).getPlayer1Id().longValue());
        assertEquals(16L, savedMatches.get(0).getPlayer2Id().longValue());

        // Match 2
        assertEquals(7L, savedMatches.get(1).getPlayer1Id().longValue());
        assertEquals(10L, savedMatches.get(1).getPlayer2Id().longValue());

        // Match 3
        assertEquals(5L, savedMatches.get(2).getPlayer1Id().longValue());
        assertEquals(12L, savedMatches.get(2).getPlayer2Id().longValue());

        // Match 4
        assertEquals(3L, savedMatches.get(3).getPlayer1Id().longValue());
        assertEquals(14L, savedMatches.get(3).getPlayer2Id().longValue());

        // Match 5
        assertEquals(4L, savedMatches.get(4).getPlayer1Id().longValue());
        assertEquals(13L, savedMatches.get(4).getPlayer2Id().longValue());

        // Match 6
        assertEquals(6L, savedMatches.get(5).getPlayer1Id().longValue());
        assertEquals(11L, savedMatches.get(5).getPlayer2Id().longValue());

        // Match 7
        assertEquals(8L, savedMatches.get(6).getPlayer1Id().longValue());
        assertEquals(9L, savedMatches.get(6).getPlayer2Id().longValue());

        // Match 8
        assertEquals(2L, savedMatches.get(7).getPlayer1Id().longValue());
        assertEquals(15L, savedMatches.get(7).getPlayer2Id().longValue());
    }


      @Test
    public void testPromotePlayersToNextRound() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setId(1L);

        // Mocking the repository to return the current round
        when(matchRepository.findMaxRoundByTournamentId(1L)).thenReturn(3);

        // Mocking matches for the current round
        Match match1 = new Match(1L, 3, 1L, 1L, 2L, LocalDate.now(), null, null, 10, 5, 1L);
        Match match2 = new Match(2L, 3, 1L, 3L, 4L, LocalDate.now(), null, null, 8, 9, 4L);
        when(matchRepository.findByTournamentIdAndRoundNo(1L, 3)).thenReturn(Arrays.asList(match1, match2));

        // Act
        List<Long> winners = matchService.promotePlayersToNextRound(tournament);

        // Assert
        assertEquals(2, winners.size());
        assertEquals(Long.valueOf(1L), winners.get(0)); // Winner of match1
        assertEquals(Long.valueOf(4L), winners.get(1)); // Winner of match2

        // Verify that new matches for the next round were created and saved
        verify(matchRepository, times(1)).saveAll(Mockito.anyList());
    }

    @Test
    public void testPromotePlayersToNextRoundWithTwoWinners() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setId(1L);

        // Mocking the repository to return the current round
        when(matchRepository.findMaxRoundByTournamentId(1L)).thenReturn(3);

        // Mocking matches for the current round
        Match match1 = new Match(1L, 3, 1L, 1L, 3L, LocalDate.now(), null, null, 10, 5, 1L); // Winner: 1L
        Match match2 = new Match(2L, 3, 1L, 2L, 4L, LocalDate.now(), null, null, 8, 9, 4L); // Winner: 4L
        when(matchRepository.findByTournamentIdAndRoundNo(1L, 3)).thenReturn(Arrays.asList(match1, match2));

        // Act
        List<Long> winners = matchService.promotePlayersToNextRound(tournament);

        // Assert
        assertEquals(2, winners.size());
        assertEquals(Long.valueOf(1L), winners.get(0)); // Winner of match1
        assertEquals(Long.valueOf(4L), winners.get(1)); // Winner of match2

        // Verify that two new matches were created and saved: one for gold/silver, one for bronze
        ArgumentCaptor<List<Match>> matchCaptor = ArgumentCaptor.forClass(List.class);
        verify(matchRepository, times(1)).saveAll(matchCaptor.capture());

        List<Match> createdMatches = matchCaptor.getValue();
        assertEquals(2, createdMatches.size());

        // Check the gold/silver match
        Match goldSilverMatch = createdMatches.get(0);
        assertEquals(1L, goldSilverMatch.getPlayer1Id());
        assertEquals(4L, goldSilverMatch.getPlayer2Id());

        // Check the bronze match
        Match bronzeMatch = createdMatches.get(1);
        assertEquals(3L, bronzeMatch.getPlayer1Id()); // Loser of match1
        assertEquals(2L, bronzeMatch.getPlayer2Id()); // Loser of match2
    }

    @Test
    public void testredefinedRounds_17Players() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> participantPlayerIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L,17L);
        when(playerService.getRegisteredPlayerIds(tournamentId)).thenReturn(participantPlayerIds);

        // Act
        int predefinedRounds = matchService.predefinedRounds(tournamentId);

        // Assert
        assertEquals(7, predefinedRounds, "The number of predefined rounds for 16 players should be 7.");
    }

    @Test
    public void testredefinedRounds_10Players() {
        // Arrange
        Long tournamentId = 1L;
        List<Long> participantPlayerIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        when(playerService.getRegisteredPlayerIds(tournamentId)).thenReturn(participantPlayerIds);

        // Act
        int predefinedRounds = matchService.predefinedRounds(tournamentId);

        // Assert
        assertEquals(6, predefinedRounds, "The number of predefined rounds for 10 players should be 6.");
    }

}