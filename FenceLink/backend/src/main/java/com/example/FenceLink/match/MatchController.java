package com.example.FenceLink.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentService;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final TournamentService tournamentService; 

    @Autowired
    public MatchController(MatchService matchService, TournamentService tournamentService) {
        this.matchService = matchService;
        this.tournamentService = tournamentService; 
    }

    /**
     * Retrieves all matches.
     */
    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    /**
     * Retrieves a match by its ID.
     */
    @GetMapping("/{matchId}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long matchId) {
        Optional<Match> match = matchService.getMatchById(matchId);
        return match.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all matches for a specific tournament.
     */
    @GetMapping("/tournament/{tournamentId}")
    public List<Match> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        return matchService.getMatchesByTournamentId(tournamentId);
    }

    /**
     * Creates a new match.
     */
    @PostMapping
    public Match createMatch(@RequestBody Match match) {
        return matchService.createMatch(match);
    }

    /**
     * Updates a match by its ID.
     */
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

    /**
     * Deletes a match by its ID.
     */
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Generates matches for the first round of a tournament.
     */
    @PostMapping("/generate/{tournamentId}")
    public ResponseEntity<String> generateMatchesForTournament(@PathVariable Long tournamentId) {
        try {
            matchService.generateMatches(tournamentId);
            return ResponseEntity.ok("First round matches generated successfully for tournament ID: " + tournamentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating matches: " + e.getMessage());
        }
    }

    /**
     * Updates the results of a match.
     */
    @PutMapping("/{matchId}/results")
    public ResponseEntity<Match> updateMatchResults(
        @PathVariable Long matchId,
        @RequestParam int player1Points,
        @RequestParam int player2Points
    ) {
        try {
            Match updatedMatch = matchService.updateMatchResults(matchId, player1Points, player2Points);
            return ResponseEntity.ok(updatedMatch);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Generates seeding matches for a tournament.
     */
    @PostMapping("/generate-seeding")
    public void generateSLMatches(@RequestParam Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament not found with ID: " + tournamentId);
        }

        matchService.generateSLMatches(tournament);
    }

    /**
     * Generates DE matches for a tournament.
     */
    @PostMapping("/generate-de-matches/{tournamentId}")
    public ResponseEntity<String> generateDEMatches(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) {
            return ResponseEntity.badRequest().body("Invalid tournament ID");
        }

        matchService.generateDEMatches(tournament);
        return ResponseEntity.ok("DE matches generated successfully");
    }
    
    /**
     * Promotes players to the next round in a tournament.
     */
    @PostMapping("/promote-players/{tournamentId}")
    public ResponseEntity<String> promotePlayersToNextRound(@PathVariable Long tournamentId) {
        try {
            Tournament tournament = tournamentService.getTournamentById(tournamentId);
            if (tournament == null) {
                return ResponseEntity.badRequest().body("Invalid tournament ID");
            }

            List<Long> winners = matchService.promotePlayersToNextRound(tournament);

            return ResponseEntity.ok("Players promoted successfully. Winners: " + winners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error promoting players: " + e.getMessage());
        }
    }

    /**
     * Fetches winners for a specific tournament.
     */
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

