package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.FenceLink.tournament.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerServiceImpl playerService;

    // Get all players
    @GetMapping("/all")
    public List<Player> getAllPlayers() {
        System.out.println("hii");
        return playerService.findAll();
    }

    // Get player by ID
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        Player player = playerService.findById(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // Add new player
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> addPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.insertPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
    }

    // Update player details for PLAYER
    @PutMapping("/{id}/edit")
    public ResponseEntity<String> updatePlayer(@PathVariable Long id, @RequestBody Player player, Principal principal) {
        Player existingPlayer = playerService.findById(id);
        if (existingPlayer == null) {
            return new ResponseEntity<>("Player not found!", HttpStatus.NOT_FOUND);
        }

        // Only players can update their own details
        if (!principal.getName().equals(existingPlayer.getName())) {
            return new ResponseEntity<>("Unauthorized to update this player's details!", HttpStatus.FORBIDDEN);
        }

        // Players can update only the following fields
        existingPlayer.setName(player.getName());
        existingPlayer.setBirthdate(player.getBirthdate());
        existingPlayer.setGender(player.getGender());
        existingPlayer.setBio(player.getBio());
        existingPlayer.setLocation(player.getLocation());
        existingPlayer.setFencingWeapon(player.getFencingWeapon());
        existingPlayer.setCountry(player.getCountry());

        // Checks validity of info through:
        playerService.updatePlayer(id, existingPlayer);
        return new ResponseEntity<>("Player details updated successfully", HttpStatus.OK);
    }

    // // Update player's points for ADMIN
    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/{id}/updatePoints")
    // public ResponseEntity<String> updatePlayerAdminFields(@PathVariable Long id, @RequestBody Player player) {
    //     Player existingPlayer = playerService.findById(id);
    //     if (existingPlayer == null) {
    //         return new ResponseEntity<>("Player not found!", HttpStatus.NOT_FOUND);
    //     }

    //     // Only admins can update the points
    //     existingPlayer.setPoints(player.getPoints());

    //     playerService.updatePlayer(id, existingPlayer);
    //     return new ResponseEntity<>("Player statistics updated successfully", HttpStatus.OK);
    // }

    // Delete player for ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable Long id) {
        Player player = playerService.findById(id);
        if (player == null) {
            return new ResponseEntity<>("Player not found!", HttpStatus.NOT_FOUND);
        }
        playerService.deletePlayerById(id);
        return new ResponseEntity<>("Player deleted successfully", HttpStatus.OK);
    }

    // Register a player for a tournament
    @PostMapping("/{playerId}/register/{tournamentId}")
    public ResponseEntity<String> registerForTournament(@PathVariable Long playerId, @PathVariable Long tournamentId) {
        try {
            String successMessage = playerService.registerPlayerForTournament(playerId, tournamentId);
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Withdraw a player from a tournament
    @DeleteMapping("/{playerId}/withdraw/{tournamentId}")
    public ResponseEntity<String> withdrawFromTournament(@PathVariable Long playerId, @PathVariable Long tournamentId) {
        try {
            String successMessage = playerService.withdrawPlayerFromTournament(playerId, tournamentId);
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Player view a list of upcoming tournaments they can register for
    @GetMapping("/{playerId}/upcoming-tournaments")
    public ResponseEntity<List<Tournament>> viewUpcomingTournaments(@PathVariable Long playerId) {
        Player player = playerService.findById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Tournament> upcomingTournaments = playerService.findUpcomingTournaments(playerId);
        return new ResponseEntity<>(upcomingTournaments, HttpStatus.OK);
    }

    // Get player's registered upcoming tournaments
    @GetMapping("/{playerId}/upcoming-registered-tournaments")
    public ResponseEntity<List<Tournament>> getUpcomingRegisteredTournaments(@PathVariable Long playerId) {
        try {
            List<Tournament> upcomingTournaments = playerService.findUpcomingTournamentsForPlayer(playerId);
            return new ResponseEntity<>(upcomingTournaments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}