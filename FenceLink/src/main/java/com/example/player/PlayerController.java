package com.example.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PutMapping("/{id}/edit")
    public ResponseEntity<String> editPlayerDetails(@PathVariable("id") String playerId, @RequestBody Player player) {
        try {
            playerService.editPlayerDetails(playerId, player);
            return new ResponseEntity<>("Player details updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}