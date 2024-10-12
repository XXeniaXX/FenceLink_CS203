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
    private PlayerServiceImpl playerService;

    // @Autowired
    // private RankingService rankingService;

    // Get all players
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.findAll();
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> addPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.insertPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
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
        playerService.deletePlayerById(id);
        return new ResponseEntity<>("Player deleted successfully", HttpStatus.OK);
    }
}