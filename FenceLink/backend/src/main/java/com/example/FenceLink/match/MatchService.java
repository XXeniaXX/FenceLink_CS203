package com.example.FenceLink.match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.FenceLink.player.Player;
import com.example.FenceLink.player.PlayerServiceImpl;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;
import com.example.FenceLink.MatchRank.MatchRank;
import com.example.FenceLink.MatchRank.MatchRankService;
import com.example.FenceLink.leaderboard.Leaderboard;
import com.example.FenceLink.MatchRank.MatchRankRepository;
import com.example.FenceLink.player.PlayerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    private final MatchRankService matchRankService;
    private final MatchRankRepository matchRankRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository, PlayerServiceImpl playerService,MatchRankService matchRankService, MatchRankRepository matchRankRepository,PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.playerService = playerService;
        this.matchRankService=matchRankService;
        this.matchRankRepository=matchRankRepository;
        this.playerRepository=playerRepository;
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

        // Initialize the match rankings
        matchRankService.initializeMatchRanks(tournamentId);
    
        // Generate Round Robin matches and log pools and matchups
        generateRoundRobinMatches(tournament, registeredPlayerIds, 1);
    }

    private void generateRoundRobinMatches(Tournament tournament, List<Long> playerIds, int roundNo) {
        String tournamentType = tournament.getTournamentType();
        List<List<Long>> pools = createPools(playerIds, tournamentType); // Pass tournamentType as an argument
    
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

    private List<List<Long>> createPools(List<Long> playerIds, String tournamentType) {
        List<List<Long>> pools = new ArrayList<>();
        List<Long> orderedPlayers;
    
        if ("competitive".equalsIgnoreCase(tournamentType)) {
            // Sort players by ELO points in descending order
            orderedPlayers = new ArrayList<>(playerIds);
            orderedPlayers.sort((p1, p2) -> Integer.compare(
                playerService.findById(p2).getPoints(),
                playerService.findById(p1).getPoints()
            ));
        } else {
            // Shuffle players for friendly tournaments
            orderedPlayers = new ArrayList<>(playerIds);
            Collections.shuffle(orderedPlayers);
        }
        int totalPlayers = orderedPlayers.size();
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
            pools.add(new ArrayList<>(orderedPlayers.subList(startIndex, endIndex)));
            startIndex = endIndex;
        }
    
        return pools;
    }
    
    public Match updateMatchResults(Long matchId, int player1Points, int player2Points) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match not found for ID: " + matchId));
    
        // Check if both player's points are provided
        if (player1Points <= -1 || player2Points <= -1) {
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
        Long winnerId, loserId;
        if (player1Points > player2Points) {
            winnerId = match.getPlayer1Id();
            loserId = match.getPlayer2Id();
        } else {
            winnerId = match.getPlayer2Id();
            loserId = match.getPlayer1Id();
        }
        match.setWinner(winnerId);
    
        // Save the updated match
        matchRepository.save(match);
    
        // Determine if the current round is the semi-final round
        int currentRoundNo = match.getRoundNo();
        int predefinedRounds = predefinedRounds(match.getTournament().getId());
    
        // Update MatchRank for the winner
        MatchRank winnerRank = matchRankRepository.findByTournamentIdAndPlayerId(match.getTournament().getId(), winnerId)
            .orElseThrow(() -> new IllegalArgumentException("MatchRank not found for player ID: " + winnerId));
        winnerRank.setWinCount(winnerRank.getWinCount() + 1);
        matchRankRepository.save(winnerRank);
    
        // Update MatchRank for the loser with elimination conditions
        MatchRank loserRank = matchRankRepository.findByTournamentIdAndPlayerId(match.getTournament().getId(), loserId)
            .orElseThrow(() -> new IllegalArgumentException("MatchRank not found for player ID: " + loserId));
        loserRank.setLossCount(loserRank.getLossCount() + 1);
    
        // Apply elimination conditions
        if (currentRoundNo > 1 && currentRoundNo != predefinedRounds - 2) { // Only eliminate if it's not the semi-final round
            loserRank.setEliminated(true);
        }
    
        // Save the updated MatchRank object for the loser
        matchRankRepository.save(loserRank);
    
        // ELO point update logic (only for competitive tournaments)
        if ("competitive".equalsIgnoreCase(match.getTournament().getTournamentType())) {
            Player player1 = playerService.findById(match.getPlayer1Id());
            Player player2 = playerService.findById(match.getPlayer2Id());
    
            double result1 = (winnerId.equals(player1.getId())) ? 1.0 : 0.0; // Determine match result for Player 1
    
            // Update ELO points using Leaderboard class
            Leaderboard leaderboard = new Leaderboard();
            leaderboard.updatePlayerPointsByElo(player1, player2, result1);
    
            // Log ELO updates
            logger.info("ELO update for match ID {}: Player {} (new points: {}), Player {} (new points: {})",
            matchId, player1.getId(), player1.getPoints(), player2.getId(), player2.getPoints());
    
            // Save the updated player points
            playerRepository.save(player1);
            playerRepository.save(player2);
    
            
        }return matchRepository.save(match);
    
    }
    


    public void generateSLMatches(Tournament tournament) {
        Long tournamentId = tournament.getId();

        String tournamentType = tournament.getTournamentType();
        if("Competitive".equalsIgnoreCase(tournamentType)){
            matchRankService.updateCMCurrentRank(tournamentId);
        }else{
            matchRankService.updateCurrentRank(tournamentId);
        }

        // Get ranked player IDs after updating ranks
        List<Long> rankedPlayerIds = matchRankService.getRankedPlayerIds(tournamentId);

        int playerCount = rankedPlayerIds.size();
        int targetCount = playerCount > 16 ? 32 : 16;
        // Calculate the number of players who need to fight for a spot
        int playersToSeed = targetCount - playerCount;
    
        // Collect the player IDs who get a direct pass (top-ranked players)
        List<Long> playersWithPass = new ArrayList<>(rankedPlayerIds.subList(0, playersToSeed));
    
        // Create seeding matches for the rest of the players
        int remainingPlayersStartIndex = playersToSeed;
        int remainingPlayersCount = playerCount - playersToSeed;
    
        for (int i = 0; i < remainingPlayersCount / 2; i++) {
            Long player1Id = rankedPlayerIds.get(remainingPlayersStartIndex + i);
            Long player2Id = rankedPlayerIds.get(remainingPlayersStartIndex + remainingPlayersCount - i - 1);
    
            Match match = new Match();
            match.setTournament(tournament);
            match.setRoundNo(2); // Assuming seeding round is round 2
            match.setPlayer1Id(player1Id);
            match.setPlayer2Id(player2Id);
            match.setDate(LocalDate.now()); // You can adjust the date as needed
            matchRepository.save(match);
        }
    
        // Add matches for players who get a direct pass
        for (Long playerId : playersWithPass) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setRoundNo(2); // Assuming pass round is round 2
            match.setPlayer1Id(playerId);
            match.setPlayer2Id(0L); // Special ID for the "Pass" opponent
            match.setDate(LocalDate.now()); // You can adjust the date as needed
            match.setPlayer1points(0); // Default 0 points for a pass
            match.setPlayer2points(0); // Default 0 points for the "Pass" opponent
            match.setWinner(playerId); // The player with the pass is the winner
            matchRepository.save(match);
             // Increment the win count for the player with the pass
            MatchRank playerRank = matchRankRepository.findByTournamentIdAndPlayerId(tournamentId, playerId)
                .orElseThrow(() -> new IllegalArgumentException("MatchRank not found for player ID: " + playerId));
            playerRank.setWinCount(playerRank.getWinCount() + 1);
            matchRankRepository.save(playerRank);
        }
    
        // Log the players who get a direct pass
        logger.info("Players who get a direct pass: {}", playersWithPass);
    }

    public void generateDEMatches(Tournament tournament) {
        Long tournamentId = tournament.getId();
        // Update player ranks before starting the DE matches

        String tournamentType = tournament.getTournamentType();
        if("Competitive".equalsIgnoreCase(tournamentType)){
            matchRankService.updateCMCurrentRank(tournamentId);
        }else{
            matchRankService.updateCurrentRank(tournamentId);
        }
        
        List<Long> rankedPlayerIds = matchRankService.getRankedPlayerIds(tournamentId);

        // If there's an odd number of players, the last one is excluded and considered eliminated
        if (rankedPlayerIds.size() % 2 != 0) {
            Long excludedPlayerId = rankedPlayerIds.remove(rankedPlayerIds.size() - 1); // Remove the last player
            // Update the MatchRank for the excluded player to mark them as eliminated
            MatchRank excludedPlayerRank = matchRankRepository.findByTournamentIdAndPlayerId(tournamentId, excludedPlayerId)
                .orElseThrow(() -> new IllegalArgumentException("MatchRank not found for player ID: " + excludedPlayerId));
            excludedPlayerRank.setEliminated(true);
            matchRankRepository.save(excludedPlayerRank);
        }
    
        int roundId = 3; // Starting round for DE matches
        List<Match> basket1 = new ArrayList<>();
        List<Match> basket2 = new ArrayList<>();
        int size = rankedPlayerIds.size();
    
        // Step 1: Split the pairs into two baskets
        for (int i = 0; i < size / 2; i++) {
            Long player1Id = rankedPlayerIds.get(i);
            Long player2Id = rankedPlayerIds.get(size - i - 1);
    
            Match match = new Match();
            match.setTournament(tournament);
            match.setRoundNo(roundId);
            match.setPlayer1Id(player1Id);
            match.setPlayer2Id(player2Id);
            match.setDate(LocalDate.now());
    
            if (i % 2 == 0) {
                basket1.add(match);
            } else {
                basket2.add(match);
            }
        }
    
        // Step 2: Rearrange the matches within the baskets using rearrangeBaskets
        List<Match> reorderedMatches = rearrangeBaskets(basket1, basket2);
    
        // Save the reordered matches in this new order
        for (Match match : reorderedMatches) {
            matchRepository.save(match);
        }
    }

    public List<Long> promotePlayersToNextRound(Tournament tournament) { 
        // Fetch the current round ID (latest round number)
        Integer currentRoundId = matchRepository.findMaxRoundByTournamentId(tournament.getId());
        int predefinedRound=predefinedRounds(tournament.getId());
        if (currentRoundId == null) {
            throw new IllegalStateException("No matches found for the given tournament.");
        }
        if ((currentRoundId == predefinedRound) || (currentRoundId == predefinedRound-1)) {
            throw new IllegalStateException("Match has been finalise,you cannot generate more macthes.");
        }
    
        List<Match> matches = matchRepository.findByTournamentIdAndRoundNo(tournament.getId(), currentRoundId);
        List<Long> winners = new ArrayList<>();
    
        // Collect all winners from the matches
        for (Match match : matches) {
            winners.add(match.getWinner());
        }
    
        // Check if this is the final round (only 2 winners)
        if (winners.size() == 2) {
            // Create the gold/silver match
            Match goldSilverMatch = new Match();
            goldSilverMatch.setTournament(tournament);
            goldSilverMatch.setRoundNo(currentRoundId + 2); // Next next round for final match
            goldSilverMatch.setPlayer1Id(winners.get(0));
            goldSilverMatch.setPlayer2Id(winners.get(1));
            goldSilverMatch.setDate(LocalDate.now()); // Adjust as needed
    
            // Collect previous opponents for the two winners
            List<Long> previousOpponents = new ArrayList<>();
            for (Match match : matches) {
                if (match.getWinner().equals(winners.get(0))) {
                    previousOpponents.add(match.getPlayer1Id().equals(winners.get(0)) ? match.getPlayer2Id() : match.getPlayer1Id());
                } else if (match.getWinner().equals(winners.get(1))) {
                    previousOpponents.add(match.getPlayer1Id().equals(winners.get(1)) ? match.getPlayer2Id() : match.getPlayer1Id());
                }
            }
    
            // Create the bronze match between previous opponents
            Match bronzeMatch = new Match();
            bronzeMatch.setTournament(tournament);
            bronzeMatch.setRoundNo(currentRoundId + 1); //next round of the bronze match
            bronzeMatch.setPlayer1Id(previousOpponents.get(0));
            bronzeMatch.setPlayer2Id(previousOpponents.get(1));
            bronzeMatch.setDate(LocalDate.now()); // Adjust as needed
    
            // Save the matches for gold/silver and bronze rounds
            matchRepository.saveAll(Arrays.asList(goldSilverMatch, bronzeMatch));
        } else {
            // Standard promotion logic for all other rounds
            int nextRoundId = currentRoundId + 1;
            List<Match> nextRoundMatches = new ArrayList<>();
            for (int i = 0; i < winners.size(); i += 2) {
                Match nextMatch = new Match();
                nextMatch.setTournament(tournament);
                nextMatch.setRoundNo(nextRoundId);
                nextMatch.setPlayer1Id(winners.get(i));
                nextMatch.setPlayer2Id(winners.get(i + 1));
                nextMatch.setDate(LocalDate.now()); // Adjust as needed
    
                nextRoundMatches.add(nextMatch);
            }
    
            // Save the matches for the next round
            matchRepository.saveAll(nextRoundMatches);
        }
    
        return winners; // Return the list of winners if needed
    }

    public int predefinedRounds(Long tournamentId) {
        // Fetch the list of participating players from PlayerServiceImpl
        List<Long> participantPlayerIds = playerService.getRegisteredPlayerIds(tournamentId);
        int playerCount = participantPlayerIds.size();
    
        // Minimum rounds: Round 1 + Seeding Round
        int predefinedRounds = 2;
    
        if (playerCount >= 32) {
            predefinedRounds += 6; // DE-32, DE-16, Quarterfinals, Semifinals, Bronze Match, Finals (Gold/Silver)
        } else if (playerCount >= 16) {
            predefinedRounds += 5; // DE-16, Quarterfinals, Semifinals, Bronze Match, Finals (Gold/Silver)
        } else if (playerCount >=10) {
            predefinedRounds += 4; // DE-8, DE-4, Bronze Match, Finals (Gold/Silver)
        }
        return predefinedRounds;
    }

    public List<Match> rearrangeBaskets(List<Match> basket1, List<Match> basket2) {
        List<Match> rearrangedBasket1 = new ArrayList<>();
        List<Match> rearrangedBasket2 = new ArrayList<>();
    
        // Basket 1: Keep the first match (highest-ranked by index) in place, then sort remaining matches in decreasing index order
        rearrangedBasket1.add(basket1.get(0)); // First match stays in place
        List<Match> remainingBasket1 = new ArrayList<>(basket1.subList(1, basket1.size()));
        remainingBasket1.sort((m1, m2) -> Integer.compare(basket1.indexOf(m2), basket1.indexOf(m1))); // Sort by decreasing index
        rearrangedBasket1.addAll(remainingBasket1);
    
        // Basket 2: Sort by increasing index order, but keep the first element (highest-ranked by index) at the end
        basket2.sort((m1, m2) -> Integer.compare(basket2.indexOf(m1), basket2.indexOf(m2))); // Sort by increasing index
        rearrangedBasket2.addAll(basket2.subList(1, basket2.size())); // Add all except the top-ranked match
        rearrangedBasket2.add(basket2.get(0)); // Place the highest-ranked match at the bottom
    
        // Combine both baskets
        List<Match> finalMatchList = new ArrayList<>();
        finalMatchList.addAll(rearrangedBasket1);
        finalMatchList.addAll(rearrangedBasket2);
    
        return finalMatchList;
    }  

    public Map<String, Long> fetchWinner(Long tournamentId) {
        // Fetch the predefined number of rounds for the tournament
        int predefinedRounds = predefinedRounds(tournamentId);

        // Find the current max round number for the tournament
        Integer currentRound = matchRepository.findMaxRoundByTournamentId(tournamentId);
        
        // If the current round matches the predefined rounds, the match is completed
        if (currentRound != null && currentRound.equals(predefinedRounds)) {
            // Fetch the last two matches of the completed tournament, ordered by matchId descending
            List<Match> lastMatches = matchRepository.findTop2ByTournamentIdOrderByMatchIdDesc(tournamentId);

            if (lastMatches.size() >= 2) {
                // Assuming matches are sorted by matchId in descending order
                Match goldSilverMatch = lastMatches.get(1);
                Match bronzeMatch = lastMatches.get(0);

                // Determine the winners and losers
                Long goldWinnerId = goldSilverMatch.getWinner();
                Long silverLoserId = goldSilverMatch.getPlayer1Id().equals(goldWinnerId) ? goldSilverMatch.getPlayer2Id() : goldSilverMatch.getPlayer1Id();
                Long bronzeWinnerId = bronzeMatch.getWinner();

                // Prepare the result map
                Map<String, Long> winners = new HashMap<>();
                winners.put("Gold", goldWinnerId);
                winners.put("Silver", silverLoserId);
                winners.put("Bronze", bronzeWinnerId);

                return winners;
            } else {
                throw new IllegalStateException("Not enough matches to determine winners.");
            }
        } else {
            throw new IllegalStateException("Tournament is not yet completed.");
        }
}
}