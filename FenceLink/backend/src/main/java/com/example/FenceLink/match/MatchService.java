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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


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
    
    public List<Long> getPlayerRank(List<Match> matches) {
        // Map to store the win and loss count for each player
        Map<Long, Integer> winCountMap = new HashMap<>();
        Map<Long, Integer> lossCountMap = new HashMap<>();
    
        // Check for matches with null winners and throw an exception if found
        for (Match match : matches) {
            if (match.getWinner() == null) {
                throw new IllegalStateException("All matches need to be completed to calculate rankings.");
            }
        }
    
        // Count wins and losses for each player
        for (Match match : matches) {
            Long winnerId = match.getWinner();
            Long player1Id = match.getPlayer1Id();
            Long player2Id = match.getPlayer2Id();
    
            // Initialize counts to 0 if the player IDs are not already in the maps
            winCountMap.putIfAbsent(player1Id, 0);
            winCountMap.putIfAbsent(player2Id, 0);
            lossCountMap.putIfAbsent(player1Id, 0);
            lossCountMap.putIfAbsent(player2Id, 0);
    
            if (winnerId.equals(player1Id)) {
                winCountMap.put(player1Id, winCountMap.get(player1Id) + 1);
                lossCountMap.put(player2Id, lossCountMap.get(player2Id) + 1);
            } else if (winnerId.equals(player2Id)) {
                winCountMap.put(player2Id, winCountMap.get(player2Id) + 1);
                lossCountMap.put(player1Id, lossCountMap.get(player1Id) + 1);
            }
        }
    
        // Create a list of player IDs from the winCountMap
        List<Long> playerIds = new ArrayList<>(winCountMap.keySet());
    
        // Sort the player IDs first by wins (descending), then by losses (ascending)
        playerIds.sort((id1, id2) -> {
            int winCompare = Integer.compare(winCountMap.get(id2), winCountMap.get(id1)); // More wins rank higher
            if (winCompare == 0) {
                int lossCompare = Integer.compare(lossCountMap.get(id1), lossCountMap.get(id2)); // Fewer losses rank higher
                if (lossCompare == 0) {
                    return new Random().nextInt(2) * 2 - 1; // Randomly choose -1 or 1 if both are equal
                }
                return lossCompare;
            }
            return winCompare;
        });
    
        // Log the ranking for each player
        logger.info("Player Rankings:");
        for (int rank = 0; rank < playerIds.size(); rank++) {
            Long playerId = playerIds.get(rank);
            int wins = winCountMap.getOrDefault(playerId, 0);
            int losses = lossCountMap.getOrDefault(playerId, 0);
            logger.info("Rank {}: Player ID: {}, Wins: {}, Losses: {}", rank + 1, playerId, wins, losses);
        }
    
        return playerIds;
    }
    
    public void updateMatchResults(Long matchId, int player1Points, int player2Points) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match not found for ID: " + matchId));
    
        // Check if both player's points are provided
        if (player1Points < 0 || player2Points < 0) {
            throw new IllegalArgumentException("Both players' points must be provided and cannot be negative.");
        }
    
        // Check if the points are equal
        if (player1Points == player2Points) {
            throw new IllegalArgumentException("Points cannot be equal. A winner must be determined.");
        }
    
        // Set points for both players
        match.setPlayer1points(player1Points);
        match.setPlayer2points(player2Points);
    
        // Determine and set the winner
        if (player1Points > player2Points) {
            match.setWinner(match.getPlayer1Id());
        } else {
            match.setWinner(match.getPlayer2Id());
        }
    
        // Save the updated match
        matchRepository.save(match);
    }

    public void generateSeedingMatches(Tournament tournament, List<Long> rankedPlayerIds) {
    int playerCount = rankedPlayerIds.size();
    int targetCount = playerCount > 16 ? 32 : 16;

    // Number of players who need to fight for a spot
    int playersToSeed = playerCount - (playerCount - targetCount / 2);

    // Create seeding matches (highest rank vs lowest rank)
    for (int i = 0; i < playersToSeed / 2; i++) {
        Long player1Id = rankedPlayerIds.get(i);
        Long player2Id = rankedPlayerIds.get(playersToSeed - i - 1);

        Match match = new Match();
        match.setTournament(tournament);
        match.setRoundNo(2); // Assuming seeding round is round 2
        match.setPlayer1Id(player1Id);
        match.setPlayer2Id(player2Id);
        match.setDate(LocalDate.now()); // You can adjust the date as needed
        matchRepository.save(match);
    }
}

    public void generateDirectEliminationMatches(Tournament tournament, List<Long> playerIds, int roundId) {
        Collections.shuffle(playerIds); // Shuffle for random matching

        for (int i = 0; i < playerIds.size() - 1; i += 2) {
            Long player1Id = playerIds.get(i);
            Long player2Id = playerIds.get(i + 1);

            Match match = new Match();
            match.setTournament(tournament);
            match.setRoundNo(roundId);
            match.setPlayer1Id(player1Id);
            match.setPlayer2Id(player2Id);
            match.setDate(LocalDate.now()); // You can adjust the date as needed
            matchRepository.save(match);
        }
    }

    public List<Long> promotePlayersToNextRound(Long tournamentId, int currentRoundId) {
        List<Match> matches = matchRepository.findByTournamentIdAndRoundNo(tournamentId, currentRoundId);
        List<Long> winners = new ArrayList<>();
    
        for (Match match : matches) {
            Long winnerId = match.getWinner();
            if (winnerId != null) {
                winners.add(winnerId);
            } else {
                throw new IllegalStateException("All matches need to be completed to determine winners.");
            }
        }
        return winners;
    }

    public void handleMedalMatches(Tournament tournament, List<Long> semifinalistIds) {
        if (semifinalistIds.size() != 4) {
            throw new IllegalArgumentException("There should be exactly 4 players for medal matches.");
        }
    
        // Semifinal matches
        Match semifinal1 = new Match();
        semifinal1.setTournament(tournament);
        semifinal1.setRoundNo(5);
        semifinal1.setPlayer1Id(semifinalistIds.get(0));
        semifinal1.setPlayer2Id(semifinalistIds.get(1));
        semifinal1.setDate(LocalDate.now());
        matchRepository.save(semifinal1);
    
        Match semifinal2 = new Match();
        semifinal2.setTournament(tournament);
        semifinal2.setRoundNo(5);
        semifinal2.setPlayer1Id(semifinalistIds.get(2));
        semifinal2.setPlayer2Id(semifinalistIds.get(3));
        semifinal2.setDate(LocalDate.now());
        matchRepository.save(semifinal2);
    
        // Bronze medal match (losers of the semifinals)
        Match bronzeMatch = new Match();
        bronzeMatch.setTournament(tournament);
        bronzeMatch.setRoundNo(6);
        bronzeMatch.setDate(LocalDate.now());
        matchRepository.save(bronzeMatch);
    
        // Gold medal match (winners of the semifinals)
        Match goldMatch = new Match();
        goldMatch.setTournament(tournament);
        goldMatch.setRoundNo(7);
        goldMatch.setDate(LocalDate.now());
        matchRepository.save(goldMatch);
    }
}