package com.example.leaderboard;

import java.util.*;
import com.example.FenceLink.player.Player;

public class Leaderboard {

    private List<Player> players;

    public Leaderboard(List<Player> players) {
        this.players = players;
    }

    // To sort players based on points in descending order (from most to least)
    public void sortPlayersByPoints() {
        players.sort(Comparator.comparingInt(Player::getPoints).reversed());
    }

    // To display leaderboard (temporary(?))
    public void displayLeaderboard() {
        sortPlayersByPoints();
        System.out.println("Leaderboard:");
        for (int i = 1; i <= players.size(); i++) {
            Player player = players.get(i-1);
            System.out.println(i + " | " + player.getName() + " | Points: " + player.getPoints());
        }
    }

    // Update points for a player
    public void updatePlayerPoints(Player player, int points) {
        player.setPoints(player.getPoints() + points);
        sortPlayersByPoints(); // Sort again
    }
}
