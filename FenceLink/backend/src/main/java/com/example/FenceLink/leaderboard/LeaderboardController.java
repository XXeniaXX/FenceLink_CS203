package com.example.FenceLink.leaderboard;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.FenceLink.player.*;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://fencelink-frontend.s3-website-ap-southeast-1.amazonaws.com")
public class LeaderboardController {

    @Autowired
    private PlayerServiceImpl playerService;

    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getTopPlayersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country) {

        Page<PlayerDTO> playerPage = playerService.getTopPlayersPage(page, size, gender, country);

        Map<String, Object> response = new HashMap<>();
        response.put("players", playerPage.getContent());
        response.put("currentPage", playerPage.getNumber());
        response.put("totalItems", playerPage.getTotalElements());
        response.put("totalPages", playerPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}