package com.example.FenceLink.MatchRank;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.player.Player;
import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class MatchRankService {

    @Autowired
    private final MatchRankRepository matchRankRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerServiceImpl playerService;
    
    public MatchRankService(MatchRankRepository matchRankRepository, TournamentRepository tournamentRepository, PlayerServiceImpl playerService) {
        this.matchRankRepository = matchRankRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerService = playerService;
    }

    public List<MatchRank> getMatchRanksByTournamentId(Long tournamentId) {
        return matchRankRepository.findByTournamentId(tournamentId);
    }

    public Optional<MatchRank> getMatchRankByTournamentIdAndPlayerId(Long tournamentId, Long playerId) {
        return matchRankRepository.findByTournamentIdAndPlayerId(tournamentId, playerId);
    }

    public MatchRank saveMatchRank(MatchRank matchRank) {
        return matchRankRepository.save(matchRank);
    }


    public void initializeMatchRanks(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new IllegalArgumentException("Tournament with ID " + tournamentId + " not found!"));
        
        List<Long> playerIds = playerService.getRegisteredPlayerIds(tournamentId);

        for (Long playerId : playerIds) {
            MatchRank matchRank = new MatchRank();
            matchRank.setTournament(tournament);
            matchRank.setPlayerId(playerId);
            matchRank.setWinCount(0);
            matchRank.setLossCount(0);
            matchRank.setCurrentRank(0);
            matchRank.setEliminated(false);

            matchRankRepository.save(matchRank);
        }
    }

    public void updateCurrentRank(Long tournamentId) {
    // Retrieve all MatchRank records for the tournament
        List<MatchRank> matchRanks = matchRankRepository.findByTournamentId(tournamentId);

        // Filter out eliminated players
        List<MatchRank> nonEliminatedPlayers = matchRanks.stream()
            .filter(rank -> !rank.isEliminated())
            .collect(Collectors.toList());

        // Sort players first by wins (descending), then by losses (ascending)
        nonEliminatedPlayers.sort((r1, r2) -> {
            int winCompare = Integer.compare(r2.getWinCount(), r1.getWinCount());
            if (winCompare == 0) {
                int lossCompare = Integer.compare(r1.getLossCount(), r2.getLossCount());
                if (lossCompare == 0) {
                    return new Random().nextInt(2) * 2 - 1; // Randomly choose -1 or 1
                }
                return lossCompare;
            }
            return winCompare;
        });

        // Update the current rank for each player
        for (int rank = 0; rank < nonEliminatedPlayers.size(); rank++) {
            nonEliminatedPlayers.get(rank).setCurrentRank(rank + 1);
            matchRankRepository.save(nonEliminatedPlayers.get(rank));
        }
    }
    public void updateCMCurrentRank(Long tournamentId) {
        // Retrieve all MatchRank records for the tournament
        List<MatchRank> matchRanks = matchRankRepository.findByTournamentId(tournamentId);
    
        // Filter out eliminated players
        List<MatchRank> nonEliminatedPlayers = matchRanks.stream()
            .filter(rank -> !rank.isEliminated())
            .collect(Collectors.toList());
    
        // Create a Random instance for tiebreaker
        Random random = new Random();
    
        // Sort players: first by wins (descending), then by losses (ascending), then by ELO points (descending)
        nonEliminatedPlayers.sort((r1, r2) -> {
            // Compare by win count (descending)
            int winCompare = Integer.compare(r2.getWinCount(), r1.getWinCount());
            if (winCompare != 0) {
                return winCompare; // If wins are different, sort by wins
            }
    
            // Compare by loss count (ascending)
            int lossCompare = Integer.compare(r1.getLossCount(), r2.getLossCount());
            if (lossCompare != 0) {
                return lossCompare; // If losses are different, sort by losses
            }
    
            // Retrieve the Player objects for ELO comparison
            Player player1 = playerService.findById(r1.getPlayerId());
            Player player2 = playerService.findById(r2.getPlayerId());
            int eloCompare = Integer.compare(player2.getPoints(), player1.getPoints());
            if (eloCompare != 0) {
                return eloCompare; // If ELO points are different, sort by ELO
            }
    
            // Randomize if all criteria are the same
            return random.nextBoolean() ? 1 : -1;
        });
    
        // Update ranks
        for (int i = 0; i < nonEliminatedPlayers.size(); i++) {
            nonEliminatedPlayers.get(i).setCurrentRank(i + 1);
        }
        matchRankRepository.saveAll(nonEliminatedPlayers);
    }

    public List<Long> getRankedPlayerIds(Long tournamentId) {
        List<MatchRank> matchRanks = matchRankRepository.findByTournamentId(tournamentId);
        
        // Filter out eliminated players and sort by current rank
        List<MatchRank> nonEliminatedRanks = matchRanks.stream()
            .filter(rank -> !rank.isEliminated())
            .sorted(Comparator.comparingInt(MatchRank::getCurrentRank))
            .collect(Collectors.toList());
    
        // Return a list of player IDs sorted by rank
        return nonEliminatedRanks.stream()
            .map(MatchRank::getPlayerId)
            .collect(Collectors.toList());
    }
}
