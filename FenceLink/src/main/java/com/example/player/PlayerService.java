package com.example.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    public Player findById(String id) {
        return playerRepository.findById(id).orElse(null);
    }

    public int addPlayer(Player player) {
        if (player.getName() == null || player.getName().isEmpty()) {
            throw new IllegalArgumentException("Player name is required");
        }

        if (player.getId() == null || player.getId().isEmpty()) {
            throw new IllegalArgumentException("Player ID is required");
        }

        return playerRepository.addPlayer(player);
    }

    public int updatePlayer(Player player) {
        // Ensures player actually exists
        if (!playerRepository.findById(player.getId()).isPresent()) {
            throw new IllegalArgumentException("Player not found!");
        }

        // Wins, losses, points cannot be negative
        if (player.getWins() < 0 || player.getLosses() < 0 || player.getPoints() < 0) {
            throw new IllegalArgumentException("Data Invalid: value cannot be negative!");
        }

        LocalDate currentDate = LocalDate.now();
        int age = Period.between(player.getBirthdate(), currentDate).getYears();

        // Birthdate should not be in the future, should be >= 14?
        if (player.getBirthdate().isAfter(currentDate)) {
            throw new IllegalArgumentException("Birthdate cannot be in te future.");
        }

        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old.")
        }

        return playerRepository.updatePlayer(player);
    }

    public int deletePlayer(Player player) {
        // Ensures player actually exists
        if (!(playerRepository.findById(player.getId()).isPresent())) {
            throw new IllegalArgumentException("Player not found!");
        }
        return playerRepository.deletePlayer(player.getId());
    }
    
    public boolean playerExists(String id) {
        return playerRepository.findById(id).isPresent();
    }

    public int editPlayerDetails(String id, Player updatedPlayer) {
        // Find existing player by ID
        Player existingPlayer = playerRepository.findById(id).orElse(null);
        
        if (existingPlayer == null) {
            throw new IllegalArgumentException("Player not found!");
        }

        // Update the player's details (only editable fields)
        existingPlayer.setName(updatedPlayer.getName());
        existingPlayer.setGender(updatedPlayer.getGender());
        existingPlayer.setCountry(updatedPlayer.getCountry());
        existingPlayer.setBirthdate(updatedPlayer.getBirthdate());
        existingPlayer.setLocation(updatedPlayer.getLocation());
        existingPlayer.setFencingWeapon(updatedPlayer.getFencingWeapon());
        existingPlayer.setBio(updatedPlayer.getBio());

        // Save updated player details
        return playerRepository.updatePlayer(existingPlayer);
    }
}
