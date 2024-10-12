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
            throw new IllegalArgumentException("Name cannot be empty!");
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
    public Player findById(Long id) {
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
    public Player updatePlayer(Long id, Player updatedPlayer) throws IllegalArgumentException {
        // Ensures player actually exists
        if (!playerExists(id)) {
            throw new IllegalArgumentException("Player not found!");
        }

        checkPlayer(updatedPlayer);

        // Id cannot be empty
        if (updatedPlayer.getId() == null) {
            throw new IllegalArgumentException("Player ID is required!");
        }

        playerRepository.saveAndFlush(updatedPlayer);
        return updatedPlayer;
    }

    // Admin only
    @Override
    @Transactional
    public void deletePlayerById(Long id) throws IllegalArgumentException {
        // Ensures player actually exists
        if (!playerExists(id)) {
            throw new IllegalArgumentException("Player not found!");
        }
        playerRepository.deletePlayerById(id);
    }

    public boolean playerExists(Long id) throws IllegalArgumentException {
        return playerRepository.findById(id).isPresent();
    }
}
