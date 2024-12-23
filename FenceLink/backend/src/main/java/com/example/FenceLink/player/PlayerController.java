package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.FenceLink.token.CognitoJWTValidator;
import com.example.FenceLink.tournament.*;

import java.util.*;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "http://localhost:3000")
public class PlayerController {

    @Autowired
    private PlayerServiceImpl playerService;


    // Get all players
    @GetMapping
    public List<Player> getAllPlayers() {
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

    // Get associated player by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Player> getPlayerByUserId(@PathVariable Long userId) {
        Player player = playerService.findByUserId(userId);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // Add new player
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> addPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.insertPlayer(player);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Player created successfully");
        response.put("playerId", savedPlayer.getId()); // Include playerId in the response

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Update the player's details
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        player.setId(id);  // Ensure player ID is set
        try {
            Player updatedPlayer = playerService.updatePlayer(id, player);

            // Calculate age based on birthdate
            int age = playerService.calculateAge(updatedPlayer.getBirthdate());

            // Prepare response with additional details
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Player updated successfully");
            response.put("name", updatedPlayer.getName());
            response.put("age", age);
            response.put("country", updatedPlayer.getCountry());
            response.put("location", updatedPlayer.getLocation());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Delete player for ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        
        String token = authorizationHeader.replace("Bearer ", "").trim();

        if(!CognitoJWTValidator.isAdmin(token)) {
            return new ResponseEntity<>("Access denied: Admin rights required", HttpStatus.FORBIDDEN);
        }

        Player player = playerService.findById(id);
        if (player == null) {
            return new ResponseEntity<>("Player not found", HttpStatus.NOT_FOUND);
        }
        playerService.deletePlayerById(id);
        return new ResponseEntity<>("Player deleted successfully", HttpStatus.OK);
    }

    // Register a player for a tournament
    @PostMapping("/{playerId}/tournaments/{tournamentId}")
    public ResponseEntity<String> registerForTournament(@PathVariable Long playerId, @PathVariable Long tournamentId) {
        try {
            String successMessage = playerService.registerPlayerForTournament(playerId, tournamentId);
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Withdraw a player from a tournament
    @DeleteMapping("/{playerId}/tournaments/{tournamentId}")
    public ResponseEntity<String> withdrawFromTournament(@PathVariable Long playerId, @PathVariable Long tournamentId) {
        try {
            String successMessage = playerService.withdrawPlayerFromTournament(playerId, tournamentId);
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get list of upcoming tournaments a player can register for
    @GetMapping("/{playerId}/tournaments/available")
    public ResponseEntity<List<UpcomingTournamentResponse>> viewUpcomingTournaments(@PathVariable Long playerId) {
        Player player = playerService.findById(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<UpcomingTournamentResponse> upcomingTournaments = playerService.findUpcomingTournaments(playerId);
        return new ResponseEntity<>(upcomingTournaments, HttpStatus.OK);
    }

    // Get player's registered upcoming tournaments
    @GetMapping("/{playerId}/tournaments/upcoming")
    public ResponseEntity<List<Tournament>> getUpcomingRegisteredTournaments(@PathVariable Long playerId) {
        try {
            List<Tournament> upcomingTournaments = playerService.findUpcomingRegisteredTournaments(playerId);
            return new ResponseEntity<>(upcomingTournaments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Get player's past tournaments they've registered for
    @GetMapping("/{playerId}/tournaments/past")
    public ResponseEntity<List<Tournament>> getPastRegisteredTournaments(@PathVariable Long playerId) {
        try {
            List<Tournament> pastTournaments = playerService.findPastRegisteredTournaments(playerId);
            return new ResponseEntity<>(pastTournaments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Player not found
        }
    }

    // Get player's id who has register for a specific tournament
    @GetMapping("/{tournamentId}/get-all-players")
    public ResponseEntity<List<Long>> getRegisteredPlayerIds(@PathVariable Long tournamentId) {
        List<Long> playerIds = playerService.getRegisteredPlayerIds(tournamentId);
        return new ResponseEntity<>(playerIds, HttpStatus.OK);
    }

    // Get all countries
    @GetMapping("/countries")
    public List<String> getAllCountries() {
        return playerService.getAllCountries();
    }
}