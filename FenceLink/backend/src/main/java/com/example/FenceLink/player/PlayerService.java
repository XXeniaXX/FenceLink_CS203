package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;

    // @Autowired
    // private ApplicationEventPublisher eventPublisher;

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    public Player findById(String id) {
        return playerRepository.findById(id).orElse(null);
    }

    public Player addPlayer(Player player) {
        // Wins and losses 0 since new player hasn't joined anything
        player.setWins(0);
        player.setLosses(0);

        if (player.getName() == null || player.getName().isEmpty()) {
            throw new IllegalArgumentException("Player name is required!");
        }

        if (player.getId() == null || player.getId().isEmpty()) {
            throw new IllegalArgumentException("Player ID is required!");
        }

        LocalDate currentDate = LocalDate.now();
        int age = Period.between(player.getBirthdate(), currentDate).getYears();

        // Birthdate should not be in the future, should be >= 14?
        if (player.getBirthdate().isAfter(currentDate)) {
            throw new IllegalArgumentException("Birthdate cannot be in the future!");
        }

        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old!");
        }

        // If player with same ID exists, cannot be added
        if (playerExists(player.getId())) {
            throw new IllegalArgumentException("Player with same ID already exists!");
        }

        Player.PlayerBuilder playerBuilder = Player.builder()
                    .id(player.getId())
                    .name(player.getName())
                    .birthdate(player.getBirthdate())
                    .wins(player.getWins())
                    .losses(player.getLosses());

                    if (player.getGender() != null) {
                        playerBuilder.bio(player.getGender());
                    }

                    if (player.getBio() != null) {
                        playerBuilder.bio(player.getBio());
                    }

                    if (player.getCountry() != null) {
                        playerBuilder.birthdate(player.getBirthdate());
                    }

                    if (player.getFencingWeapon() != null) {
                        playerBuilder.fencingWeapon(player.getFencingWeapon());
                    }

                    if (player.getLocation() != null) {
                        playerBuilder.location(player.getLocation());
                    }

                    // .ranking(player.getRanking())
                    // .points(player.getPoints())

                    playerBuilder.build();
            
        return playerRepository.save(player);
    }

    // Admin
    public Player updatePlayer(String id, Player updatedPlayer) {
        // Ensures player actually exists
        if (!playerRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Player not found!");
        }

        // Wins, losses, points cannot be negative
        if (updatedPlayer.getWins() < 0 || updatedPlayer.getLosses() < 0) {
            throw new IllegalArgumentException("Data Invalid: value cannot be negative!");
        }

        LocalDate currentDate = LocalDate.now();
        int age = Period.between(updatedPlayer.getBirthdate(), currentDate).getYears();

        // Birthdate should not be in the future, should be >= 14?
        if (updatedPlayer.getBirthdate().isAfter(currentDate)) {
            throw new IllegalArgumentException("Birthdate cannot be in the future!");
        }

        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old!");
        }

        return playerRepository.save(updatedPlayer);
    }

    // Admin only
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

        // Cannot change ID, wins, losses
        if (!(updatedPlayer.getId().equals(id))) {
            throw new IllegalArgumentException("ID cannot be changed!");
        }

        if (existingPlayer.getWins() != updatedPlayer.getWins()) {
            throw new IllegalArgumentException("You cannot change number of wins!");
        }

        if (existingPlayer.getLosses() != updatedPlayer.getLosses()) {
            throw new IllegalArgumentException("You cannot change number of Losses!");
        }

        if (updatedPlayer.getName() == null || updatedPlayer.getName().isEmpty()) {
            throw new IllegalArgumentException("Player name is required!");
        }
    
        if (updatedPlayer.getBirthdate() == null || updatedPlayer.getBirthdate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid birthdate. It cannot be in the future!");
        }
    
        int age = Period.between(updatedPlayer.getBirthdate(), LocalDate.now()).getYears();
        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old!");
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
