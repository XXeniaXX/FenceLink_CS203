package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.*;
import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    // Check player info validity
    public void checkPlayer(Player player) throws IllegalArgumentException {
        // Name cannot be empty
        if (player.getName() == null || player.getName().isEmpty()) {
            throw new IllegalArgumentException("Player name is required!");
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

        // Wins, losses, points cannot be negative
        if (player.getWins() < 0 || player.getLosses() < 0) {
            throw new IllegalArgumentException("Data Invalid: value cannot be negative!");
        }
    }

    @Override
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Override
    public Player findById(String id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Player insertPlayer(Player player) throws IllegalArgumentException {
        // Wins and losses 0 since new player hasn't joined anything
        player.setWins(0);
        player.setLosses(0);

        checkPlayer(player);

        Player.PlayerBuilder playerBuilder = Player.builder()
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

        playerRepository.saveAndFlush(player);
        return player;
    }

    // Admin only
    @Override
    @Transactional
    public Player updatePlayer(String id, Player updatedPlayer) throws IllegalArgumentException {
        // Ensures player actually exists
        if (!playerExists(id)) {
            throw new IllegalArgumentException("Player not found!");
        }

        checkPlayer(updatedPlayer);

        // Id cannot be empty
        if (updatedPlayer.getId() == null || updatedPlayer.getId().isEmpty()) {
            throw new IllegalArgumentException("Player ID is required!");
        }

        playerRepository.saveAndFlush(updatedPlayer);
        return playerRepository.findById(id).get();
    }

    // Admin only
    @Override
    @Transactional
    public void deletePlayerById(String id) throws IllegalArgumentException {
        // Ensures player actually exists
        if (!playerExists(id)) {
            throw new IllegalArgumentException("Player not found!");
        }
        playerRepository.deletePlayerById(id);
    }

    public boolean playerExists(String id) throws IllegalArgumentException {
        return playerRepository.findById(id).isPresent();
    }

    // For users
    @Override
    @Transactional
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
            throw new IllegalArgumentException("You cannot change number of losses!");
        }

        checkPlayer(updatedPlayer);

        // Id cannot be empty
        if (updatedPlayer.getId() == null || updatedPlayer.getId().isEmpty()) {
            throw new IllegalArgumentException("Player ID is required!");
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
        playerRepository.saveAndFlush(existingPlayer);
        return playerRepository.findById(id).get();
    }
}
