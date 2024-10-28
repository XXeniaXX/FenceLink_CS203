package com.example.FenceLink.leaderboard;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FenceLink.match.Match;
import com.example.FenceLink.player.*;
import com.example.FenceLink.tournament.Tournament;

@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    public List<Player> getPlayers(Leaderboard leaderboard) {
        if (leaderboard.getTournament() != null) {
            return leaderboard.getTournament().getPlayers();
        } else if (leaderboard.getMatch() != null) {
            return Arrays.asList(leaderboard.getMatch().getPlayer1(), leaderboard.getMatch().getPlayer2());
        }
        return new ArrayList<>();
    }

    // Sort players in descending order
    public void sortPlayersByPoints(Leaderboard leaderboard) {
        List<Player> players = getPlayers(leaderboard);
        players.sort(Comparator.comparingInt(Player::getPoints).reversed());
    }

    // Display the leaderboard
    public void displayLeaderboard(Leaderboard leaderboard) {
        sortPlayersByPoints(leaderboard);
        List<Player> players = getPlayers(leaderboard);
        System.out.println("Leaderboard:");
        for (int i = 1; i <= players.size(); i++) {
            Player player = players.get(i - 1);
            System.out.println(i + ". " + player.getName() + " | Points: " + player.getPoints());
        }
    }

    // Calculate the expected score for player 1
    private double expectedScore(Player player1, Player player2) {
        int points1 = player1.getPoints();
        int points2 = player2.getPoints();
        return 1 / (1 + Math.pow(10, (points2 - points1) / 400.0));
    }

    // Update points based on match results
    public void updatePlayerPointsByElo(Leaderboard leaderboard, Player player1, Player player2, double result1) {
        double k = 32; // ELO K-factor, can be adjusted based on ranking volatility

        // Calculate expected scores
        double expected1 = expectedScore(player1, player2);
        double expected2 = 1 - expected1; // Expected score for player B

        // Update points for Player 1
        int newPoints1 = (int) (player1.getPoints() + k * (result1 - expected1));
        player1.setPoints(newPoints1);

        // Update points for Player 2
        double result2 = 1 - result1;
        int newPoints2 = (int) (player2.getPoints() + k * (result2 - expected2));
        player2.setPoints(newPoints2);

        // Sort the players after updating points
        sortPlayersByPoints(leaderboard);
    }

    // Get leaderboard by tournament
    public Optional<Leaderboard> getLeaderboardByTournament(Tournament tournament) {
        return leaderboardRepository.findByTournament(tournament);
    }

    // Get leaderboard by match
    public Optional<Leaderboard> getLeaderboardByMatch(Match match) {
        return leaderboardRepository.findByMatch(match);
    }
}
