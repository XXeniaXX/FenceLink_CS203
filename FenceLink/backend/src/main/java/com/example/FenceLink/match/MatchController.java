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

    @GetMapping("/{matchId}/{roundNo}/{tournamentId}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long matchId, @PathVariable int roundNo, @PathVariable Long tournamentId) {
        MatchId id = new MatchId(matchId, roundNo, tournamentId);
        Optional<Match> match = matchService.getMatchById(id);
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

    @PutMapping("/{matchId}/{roundNo}/{tournamentId}")
    public ResponseEntity<Match> updateMatch(@PathVariable Long matchId, @PathVariable int roundNo, @PathVariable Long tournamentId, @RequestBody Match matchDetails) {
        MatchId id = new MatchId(matchId, roundNo, tournamentId);
        Optional<Match> match = matchService.getMatchById(id);

        if (match.isPresent()) {
            Match updatedMatch = match.get();
            updatedMatch.setPlayer1(matchDetails.getPlayer1());
            updatedMatch.setPlayer2(matchDetails.getPlayer2());
            updatedMatch.setDate(matchDetails.getDate());
            updatedMatch.setStartTime(matchDetails.getStartTime());
            updatedMatch.setEndTime(matchDetails.getEndTime());

            matchService.updateMatch(updatedMatch);
            return ResponseEntity.ok(updatedMatch);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{matchId}/{roundNo}/{tournamentId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId, @PathVariable int roundNo, @PathVariable Long tournamentId) {
        MatchId id = new MatchId(matchId, roundNo, tournamentId);
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}
