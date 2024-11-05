package com.example.FenceLink.MatchRank;

import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Comparator;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

public class MatchRankServiceTest {

    @Mock
    private MatchRankRepository matchRankRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private PlayerServiceImpl playerService;

    @InjectMocks
    private MatchRankService matchRankService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMatchRanksByTournamentId() {
        Long tournamentId = 1L;
        MatchRank matchRank = new MatchRank();
        matchRank.setTournament(new Tournament());
        when(matchRankRepository.findByTournamentId(tournamentId)).thenReturn(List.of(matchRank));

        List<MatchRank> matchRanks = matchRankService.getMatchRanksByTournamentId(tournamentId);

        assertEquals(1, matchRanks.size());
        verify(matchRankRepository, times(1)).findByTournamentId(tournamentId);
    }

    @Test
    public void testGetMatchRankByTournamentIdAndPlayerId() {
        Long tournamentId = 1L;
        Long playerId = 2L;
        MatchRank matchRank = new MatchRank();
        matchRank.setPlayerId(playerId);
        when(matchRankRepository.findByTournamentIdAndPlayerId(tournamentId, playerId)).thenReturn(Optional.of(matchRank));

        Optional<MatchRank> result = matchRankService.getMatchRankByTournamentIdAndPlayerId(tournamentId, playerId);

        assertTrue(result.isPresent());
        assertEquals(playerId, result.get().getPlayerId());
        verify(matchRankRepository, times(1)).findByTournamentIdAndPlayerId(tournamentId, playerId);
    }

    @Test
    public void testSaveMatchRank() {
        MatchRank matchRank = new MatchRank();
        when(matchRankRepository.save(matchRank)).thenReturn(matchRank);

        MatchRank savedMatchRank = matchRankService.saveMatchRank(matchRank);

        assertNotNull(savedMatchRank);
        verify(matchRankRepository, times(1)).save(matchRank);
    }

    @Test
    public void testInitializeMatchRanks() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        List<Long> playerIds = Arrays.asList(101L, 102L, 103L);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(playerService.getRegisteredPlayerIds(tournamentId)).thenReturn(playerIds);

        // Act
        matchRankService.initializeMatchRanks(tournamentId);

        // Assert
        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(playerService, times(1)).getRegisteredPlayerIds(tournamentId);
        verify(matchRankRepository, times(playerIds.size())).save(any(MatchRank.class));

        // Additional verification for correct initialization
        for (Long playerId : playerIds) {
            MatchRank expectedMatchRank = new MatchRank();
            expectedMatchRank.setTournament(tournament);
            expectedMatchRank.setPlayerId(playerId);
            expectedMatchRank.setWinCount(0);
            expectedMatchRank.setLossCount(0);
            expectedMatchRank.setCurrentRank(0);
            expectedMatchRank.setEliminated(false);

            verify(matchRankRepository).save(refEq(expectedMatchRank));
        }
    }

    @Test
    public void testUpdateCurrentRank() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);

        List<MatchRank> matchRanks = new ArrayList<>();
        MatchRank player1 = new MatchRank(1L, tournament, 101L, 2, 1, 0, false); // 2 wins, 1 loss
        MatchRank player2 = new MatchRank(2L, tournament, 102L, 1, 1, 0, false); // 1 win, 1 loss
        MatchRank player3 = new MatchRank(3L, tournament, 103L, 1, 1, 0, false); // 1 win, 1 loss
        MatchRank eliminatedPlayer = new MatchRank(4L, tournament, 104L, 3, 0, 0, true); // Eliminated

        matchRanks.add(player1);
        matchRanks.add(player2);
        matchRanks.add(player3);
        matchRanks.add(eliminatedPlayer);

        when(matchRankRepository.findByTournamentId(tournamentId)).thenReturn(matchRanks);

        // Act
        matchRankService.updateCurrentRank(tournamentId);

        // Assert
        verify(matchRankRepository, times(1)).findByTournamentId(tournamentId);

        // Ensure only the 3 active players are ranked
        assertEquals(1, player1.getCurrentRank());
        assertTrue((player2.getCurrentRank() == 2 && player3.getCurrentRank() == 3) ||
                (player2.getCurrentRank() == 3 && player3.getCurrentRank() == 2));
        assertEquals(0, eliminatedPlayer.getCurrentRank()); // Eliminated player should not have a rank
    }

    @Test
    public void testUpdateCurrentRank_ScoreUpdate() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);

        // Initial ranks
        MatchRank player1 = new MatchRank(1L, tournament, 101L, 2, 1, 0, false); // 2 wins, 1 loss
        MatchRank player2 = new MatchRank(2L, tournament, 102L, 1, 1, 0, false); // 1 win, 1 loss
        MatchRank player3 = new MatchRank(3L, tournament, 103L, 1, 1, 0, false); // 1 win, 1 loss

        // Add players to the list
        List<MatchRank> matchRanks = Arrays.asList(player1, player2, player3);

        // Mock repository behavior
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(matchRankRepository.findByTournamentId(tournamentId)).thenReturn(matchRanks);

        // Simulate updating scores
        player2.setWinCount(3); // Increased win count

        // Act
        matchRankService.updateCurrentRank(tournamentId);

        // Assert
        verify(matchRankRepository, times(3)).save(any(MatchRank.class));
        assertEquals(1, player2.getCurrentRank()); // Player 2 should now be rank 1
        assertEquals(2, player1.getCurrentRank()); // Player 1 should now be rank 2
        assertEquals(3, player3.getCurrentRank()); // Player 3 remains rank 3
    }

    @Test
    public void testGetRankedPlayerIds() {
        // Arrange
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);

        MatchRank player1 = new MatchRank(1L, tournament, 101L, 3, 1, 1, false); // Rank 1
        MatchRank player2 = new MatchRank(2L, tournament, 102L, 2, 2, 2, false); // Rank 2
        MatchRank player3 = new MatchRank(3L, tournament, 103L, 1, 3, 3, false); // Rank 3
        MatchRank eliminatedPlayer = new MatchRank(4L, tournament, 104L, 0, 0, 4, true); // Eliminated

        List<MatchRank> matchRanks = Arrays.asList(player1, player2, player3, eliminatedPlayer);
        
        when(matchRankRepository.findByTournamentId(tournamentId)).thenReturn(matchRanks);

        // Act
        List<Long> rankedPlayerIds = matchRankService.getRankedPlayerIds(tournamentId);

        // Assert
        assertEquals(3, rankedPlayerIds.size()); // Only 3 non-eliminated players should be returned
        assertEquals(101L, rankedPlayerIds.get(0));
        assertEquals(102L, rankedPlayerIds.get(1));
        assertEquals(103L, rankedPlayerIds.get(2));
    }
}
