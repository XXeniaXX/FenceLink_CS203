package com.example.FenceLink.MatchRank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/match-rank")
public class MatchRankController {

    @Autowired
    private MatchRankService matchRankService;

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchRank>> getMatchRanksByTournamentId(@PathVariable Long tournamentId) {
        List<MatchRank> matchRanks = matchRankService.getMatchRanksByTournamentId(tournamentId);
        return ResponseEntity.ok(matchRanks);
    }

    @GetMapping("/tournament/{tournamentId}/player/{playerId}")
    public ResponseEntity<MatchRank> getMatchRankByTournamentIdAndPlayerId(
            @PathVariable Long tournamentId, @PathVariable Long playerId) {
        Optional<MatchRank> matchRank = matchRankService.getMatchRankByTournamentIdAndPlayerId(tournamentId, playerId);
        return matchRank.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MatchRank> createMatchRank(@RequestBody MatchRank matchRank) {
        MatchRank savedMatchRank = matchRankService.saveMatchRank(matchRank);
        return ResponseEntity.ok(savedMatchRank);
    }

    
    @PostMapping("/initialize/{tournamentId}")
    public ResponseEntity<String> initializeMatchRanks(@PathVariable Long tournamentId) {
        try {
            matchRankService.initializeMatchRanks(tournamentId);
            return ResponseEntity.ok("Match ranks initialized successfully for Tournament ID: " + tournamentId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update-rank/{tournamentId}")
    public ResponseEntity<String> updateCurrentRank(@PathVariable Long tournamentId) {
        matchRankService.updateCurrentRank(tournamentId);
        return ResponseEntity.ok("Player ranks have been updated successfully.");
    }

    @GetMapping("/ranked-players/{tournamentId}")
    public ResponseEntity<List<Long>> getRankedPlayerIds(@PathVariable Long tournamentId) {
        List<Long> rankedPlayerIds = matchRankService.getRankedPlayerIds(tournamentId);
        return ResponseEntity.ok(rankedPlayerIds);
    }
}
