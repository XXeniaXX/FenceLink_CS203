package com.example.leaderboard;

import java.util.*;
import com.example.FenceLink.player.Player;
import jakarta.persistence.*;

@Entity
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    

    public Leaderboard() {}

    public Leaderboard(List<Player> players) {
        this.players = players;
    }

    // Sort players in descending order
    public void sortPlayersByPoints() {
        players.sort(Comparator.comparingInt(Player::getPoints).reversed());
    }

    // Display the leaderboard
    public void displayLeaderboard() {
        sortPlayersByPoints();
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
    public void updatePlayerPointsByElo(Player player1, Player player2, double result1) {
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
        sortPlayersByPoints();
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }
}
