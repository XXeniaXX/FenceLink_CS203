package com.example.FenceLink.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
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
            updatedMatch.setPlayer1Id(matchDetails.getPlayer1Id());
            updatedMatch.setPlayer2Id(matchDetails.getPlayer2Id());
            updatedMatch.setDate(matchDetails.getDate());
            updatedMatch.setStartTime(matchDetails.getStartTime());
            updatedMatch.setEndTime(matchDetails.getEndTime());
            updatedMatch.setPlayer1points(matchDetails.getPlayer1points());
            updatedMatch.setPlayer2points(matchDetails.getPlayer2points());
            updatedMatch.setWinner(matchDetails.getWinner());

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
    

}
