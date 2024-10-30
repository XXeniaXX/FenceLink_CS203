package com.example.FenceLink.match;

import org.apache.logging.log4j.util.PropertySource.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.FenceLink.player.Player;
import com.example.FenceLink.player.PlayerService;
import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final PlayerServiceImpl playerService; 

    @Autowired
    public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository, PlayerServiceImpl playerService) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerService = playerService;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(Long matchId) {
        return matchRepository.findById(matchId);
    }

    public List<Match> getMatchesByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    @Transactional
    public Match createMatch(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null.");
        }
        if (match.getTournament().getId()== null) {
            throw new IllegalArgumentException("Tournament ID is required but is missing.");
        }

        logger.info("Attempting to save Match with tournament ID: {}", match.getTournament().getId());

        Tournament tournament = tournamentRepository.findById(match.getTournament().getId())
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found for ID: " + match.getTournament().getId()));

        match.setTournament(tournament);
        return matchRepository.save(match);
    }

    public Match updateMatch(Match match) {
        return matchRepository.save(match);
    }

    public void deleteMatch(Long matchId) {
        matchRepository.deleteById(matchId);
    }

    //matching system
    public void generateMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new RuntimeException("Tournament not found"));
    
        List<Long> registeredPlayerIds = playerService.getRegisteredPlayerIds(tournamentId);
        int totalPlayers = registeredPlayerIds.size();
    
        if (totalPlayers < 10) {
            throw new IllegalStateException("Tournament requires a minimum of 10 players");
        }
    
        // Log the start of the matching process
        logger.info("Starting match generation for tournament ID: {}", tournamentId);
    
        // Generate Round Robin matches and log pools and matchups
        generateRoundRobinMatches(tournament, registeredPlayerIds, 1);
    }

    private void generateRoundRobinMatches(Tournament tournament, List<Long> playerIds, int roundNo) {
        List<List<Long>> pools = createPools(playerIds);
    
        for (int poolIndex = 0; poolIndex < pools.size(); poolIndex++) {
            List<Long> pool = pools.get(poolIndex);
            
            // Log the pool players
            logger.info("Pool {} contains players: {}", poolIndex + 1, pool.stream().map(Object::toString).collect(Collectors.joining(", ")));
    
            // Generate matches within each pool
            for (int i = 0; i < pool.size(); i++) {
                for (int j = i + 1; j < pool.size(); j++) {
                    Long player1Id = pool.get(i);
                    Long player2Id = pool.get(j);
    
                    // Log each match within the pool
                    logger.info("Creating match in Pool {}: Player {} vs Player {}", poolIndex + 1, player1Id, player2Id);
    
                    Match match = new Match();
                    match.setTournament(tournament);
                    match.setRoundNo(roundNo);
                    match.setPlayer1Id(player1Id);
                    match.setPlayer2Id(player2Id);
                    match.setDate(tournament.getStartDate());
    
                    // Save each match to the repository
                    matchRepository.save(match);
                }
            }
        }
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

    // private void generateDirectEliminationMatches(Tournament tournament, List<Player> players, int roundNo) {
    //     List<Player> matchedPlayers = new ArrayList<>(players);
    //     Collections.shuffle(matchedPlayers); // Random matching
        
    //     // Generate matches for this round
    //     for (int i = 0; i < matchedPlayers.size() - 1; i += 2) {
    //         Match match = Match.builder()
    //             .tournamentId(tournament.getId())
    //             .roundNo(roundNo)
    //             .player1Id(matchedPlayers.get(i).getId())
    //             .player2Id(matchedPlayers.get(i + 1).getId())
    //             .date(tournament.getStartDate())
    //             // startTime and endTime will be set by admin
    //             .build();
            
    //         matchRepository.save(match);
    //     }
    // }

    // public void promotePlayersToNextRound(Long tournamentId, int currentRound) {
    //     Tournament tournament = tournamentRepository.findById(tournamentId)
    //         .orElseThrow(() -> new RuntimeException("Tournament not found"));
            
    //     // Get matches from current round
    //     List<Match> matches = matchRepository.findByTournamentIdAndRoundNo(tournamentId, currentRound);
        
    //     // Get qualified players sorted by points
    //     List<Player> qualifiedPlayers = matches.stream()
    //         .map(match -> playerRepository.findById(match.getWinner())
    //             .orElseThrow(() -> new RuntimeException("Winner not found")))
    //         .sorted(Comparator.comparing(Player::getPoints).reversed())
    //         .collect(Collectors.toList());
        
    //     int nextRound = currentRound + 1;
        
    //     // Determine next round structure based on current round and player count
    //     if (currentRound == 1) { // After Round Robin
    //         if (qualifiedPlayers.size() >= 32) {
    //             qualifiedPlayers = qualifiedPlayers.subList(0, 32); // Top 32 advance
    //             // Round 2: DE-32
    //         } else if (qualifiedPlayers.size() >= 16) {
    //             qualifiedPlayers = qualifiedPlayers.subList(0, 16); // Top 16 advance
    //             // Round 2: DE-16
    //         } else {
    //             qualifiedPlayers = qualifiedPlayers.subList(0, 8); // Top 8 advance
    //             // Round 2: Quarterfinals
    //         }
    //     }
        
    //     // Special handling for semifinals and finals
    //     if (qualifiedPlayers.size() == 4) {
    //         // Generate semifinals (Round 5)
    //         generateDirectEliminationMatches(tournament, qualifiedPlayers, nextRound);
    //     } else if (qualifiedPlayers.size() == 2) {
    //         // For bronze medal match (Round 6)
    //         List<Player> losers = getSemifinalLosers(tournamentId);
    //         generateDirectEliminationMatches(tournament, losers, nextRound);
            
    //         // For gold medal match (Round 7)
    //         generateDirectEliminationMatches(tournament, qualifiedPlayers, nextRound + 1);
    //     } else {
    //         // Normal DE round
    //         generateDirectEliminationMatches(tournament, qualifiedPlayers, nextRound);
    //     }
    // }

    // private List<Player> getSemifinalLosers(Long tournamentId) {
    //     // Get semifinal matches and return the losers
    //     List<Match> semifinals = matchRepository.findByTournamentIdAndRoundNo(tournamentId, 5);
    //     return semifinals.stream()
    //         .map(match -> {
    //             Long loserId = match.getPlayer1Id().equals(match.getWinner()) 
    //                 ? match.getPlayer2Id() 
    //                 : match.getPlayer1Id();
    //             return playerRepository.findById(loserId)
    //                 .orElseThrow(() -> new RuntimeException("Player not found"));
    //         })
    //         .collect(Collectors.toList());
    // }
}