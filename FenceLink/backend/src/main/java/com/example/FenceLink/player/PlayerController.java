package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // @Autowired
    // private RankingService rankingService;

    // Get all players
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.findAllPlayers();
    }

    // Get player by ID
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String id) {
        Player player = playerService.findById(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    // Add new player
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> addPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.addPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
    }

    // Update player details for ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePlayer(@PathVariable String id, @RequestBody Player player) {
        player.setId(id);  // Ensure player ID is set
        try {
            playerService.updatePlayer(id, player);
            return new ResponseEntity<>("Player updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete player for ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable String id) {
        Player player = playerService.findById(id);
        if (player == null) {
            return new ResponseEntity<>("Player not found", HttpStatus.NOT_FOUND);
        }
        playerService.deletePlayer(id);
        return new ResponseEntity<>("Player deleted successfully", HttpStatus.OK);
    }

    // Edit player details for USERS
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editPlayerDetails(@PathVariable("id") String playerId, @RequestBody Player player) {
        try {
            playerService.editPlayerDetails(playerId, player);
            Player updatedPlayer = playerService.findById(playerId); // Fetch the updated player
            return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}