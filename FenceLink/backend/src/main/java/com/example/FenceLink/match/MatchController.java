package com.example.FenceLink.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final TournamentService tournamentService; // Ensure this is declared

    @Autowired
    public MatchController(MatchService matchService, TournamentService tournamentService) {
        this.matchService = matchService;
        this.tournamentService = tournamentService; // Ensure this is injected
    }

    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    // Adjusted endpoint to use only matchId
    @GetMapping("/{matchId}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long matchId) {
        Optional<Match> match = matchService.getMatchById(matchId);
        return match.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tournament/{tournamentId}")
    public List<Match> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        return matchService.getMatchesByTournamentId(tournamentId);
    }

    @PostMapping
    public Match createMatch(@RequestBody Match match) {
        return matchService.createMatch(match);
    }

    // Updated method to use only matchId
    @PutMapping("/{matchId}")
    public ResponseEntity<Match> updateMatch(@PathVariable Long matchId, @RequestBody Match matchDetails) {
        Optional<Match> match = matchService.getMatchById(matchId);

        if (match.isPresent()) {
            Match updatedMatch = match.get();
            updatedMatch.setDate(matchDetails.getDate());
            updatedMatch.setStartTime(matchDetails.getStartTime());
            updatedMatch.setEndTime(matchDetails.getEndTime());

            matchService.updateMatch(updatedMatch);
            return ResponseEntity.ok(updatedMatch);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Updated delete method to use only matchId
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }

    //For matching system
    // Endpoint to manually generate matches for a tournament
    @PostMapping("/generate/{tournamentId}")
    public ResponseEntity<String> generateMatchesForTournament(@PathVariable Long tournamentId) {
        try {
            matchService.generateMatches(tournamentId);
            return ResponseEntity.ok("First Round match generated successfully for tournament ID: " + tournamentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating matches: " + e.getMessage());
        }
    }

    @PutMapping("/{matchId}/results")
    public ResponseEntity<Map<String, Object>> updateMatchResults(
        @PathVariable Long matchId,
        @RequestParam int player1Points,
        @RequestParam int player2Points
    ) {
        try {
            // Call the service method to update match results and get the response map
            return matchService.updateMatchResults(matchId, player1Points, player2Points);
        } catch (IllegalArgumentException e) {
            // Handle bad requests due to invalid input
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(500).body(Map.of("error", "An error occurred while updating match results."));
        }
    }

     @PostMapping("/generate-seeding")
    public void generateSLMatches(@RequestParam Long tournamentId) {
        // Fetch the tournament using the provided tournamentId
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found with ID: " + tournamentId);
        }

        // Call the method to generate seeding matches
        matchService.generateSLMatches(tournament);
    }

    @PostMapping("/generate-de-matches/{tournamentId}")
    public ResponseEntity<String> generateDEMatches(@PathVariable Long tournamentId) {
        // Fetch the Tournament object by its ID
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) {
            return ResponseEntity.badRequest().body("Invalid tournament ID");
        }

        // Call the service method to generate DE matches
        matchService.generateDEMatches(tournament);
        return ResponseEntity.ok("DE matches generated successfully");
    }
    
    @PostMapping("/promote-players/{tournamentId}")
    public ResponseEntity<String> promotePlayersToNextRound(@PathVariable Long tournamentId) {
        try {
            // Fetch the Tournament object by ID
            Tournament tournament = tournamentService.getTournamentById(tournamentId);
            if (tournament == null) {
                return ResponseEntity.badRequest().body("Invalid tournament ID");
            }

            // Call the service method to promote players
            List<Long> winners = matchService.promotePlayersToNextRound(tournament);

            return ResponseEntity.ok("Players promoted successfully. Winners: " + winners);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error promoting players: " + e.getMessage());
        }
    }
    @GetMapping("/{tournamentId}/winners")
    public ResponseEntity<Map<String, Long>> fetchWinners(@PathVariable Long tournamentId) {
        try {
            Map<String, Long> winners = matchService.fetchWinner(tournamentId);
            return ResponseEntity.ok(winners);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
