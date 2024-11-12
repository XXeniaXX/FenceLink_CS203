package com.example.FenceLink.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import com.example.FenceLink.player.*;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://fencelink-frontend.s3-website-ap-southeast-1.amazonaws.com")
public class LeaderboardController {

    @Autowired
    private PlayerServiceImpl playerService;

    // Endpoint for paginated leaderboard data
    @GetMapping("/top")
    public Page<PlayerDTO> getTopPlayersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country) {
        
        return playerService.getTopPlayersPage(page, 20, gender, country); // 20 players per page
    }
}