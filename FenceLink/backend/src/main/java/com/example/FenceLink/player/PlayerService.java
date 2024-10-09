package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
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

    public Player addPlayer(Player player) {
        if (player.getName() == null || player.getName().isEmpty()) {
            throw new IllegalArgumentException("Player name is required");
        }

        if (player.getId() == null || player.getId().isEmpty()) {
            throw new IllegalArgumentException("Player ID is required");
        }

        return playerRepository.save(player);
    }

    // Admin
    public Player updatePlayer(String id, Player updatedPlayer) {
        // Ensures player actually exists
        if (!playerRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Player not found!");
        }

        // Wins, losses, points cannot be negative
        if (updatedPlayer.getWins() < 0 || updatedPlayer.getLosses() < 0 || updatedPlayer.getPoints() < 0) {
            throw new IllegalArgumentException("Data Invalid: value cannot be negative!");
        }

        LocalDate currentDate = LocalDate.now();
        int age = Period.between(updatedPlayer.getBirthdate(), currentDate).getYears();

        // Birthdate should not be in the future, should be >= 14?
        if (updatedPlayer.getBirthdate().isAfter(currentDate)) {
            throw new IllegalArgumentException("Birthdate cannot be in te future.");
        }

        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old.");
        }

        return playerRepository.save(updatedPlayer);
    }

    // Admin only (should this be deleted?)
    public void deletePlayer(String id) {
        // Ensures player actually exists
        if (!(playerRepository.findById(id).isPresent())) {
            throw new IllegalArgumentException("Player not found!");
        }
        playerRepository.deleteById(id);
    }
    
    public boolean playerExists(String id) {
        return playerRepository.findById(id).isPresent();
    }

    // For users
    public Player editPlayerDetails(String id, Player updatedPlayer) {
        // Find existing player by ID
        Player existingPlayer = playerRepository.findById(id).orElse(null);
        
        if (existingPlayer == null) {
            throw new IllegalArgumentException("Player not found!");
        }

        if (updatedPlayer.getName() == null || updatedPlayer.getName().isEmpty()) {
            throw new IllegalArgumentException("Player name is required");
        }
    
        if (updatedPlayer.getBirthdate() == null || updatedPlayer.getBirthdate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid birthdate. It cannot be in the future.");
        }
    
        int age = Period.between(updatedPlayer.getBirthdate(), LocalDate.now()).getYears();
        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old.");
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
        return playerRepository.save(existingPlayer);
    }
}
