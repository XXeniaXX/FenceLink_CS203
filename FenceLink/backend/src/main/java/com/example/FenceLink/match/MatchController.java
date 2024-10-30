package com.example.FenceLink.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.FenceLink.tournament.Tournament;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

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
    public ResponseEntity<List<Match>> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        Optional<Tournament> tournament = matchService.findTournamentById(tournamentId);
    
        if (tournament.isPresent()) {
            List<Match> matches = matchService.getMatchesByTournament(tournament.get());
            return ResponseEntity.ok(matches);
        } else {
            return ResponseEntity.notFound().build();
        }
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
}
