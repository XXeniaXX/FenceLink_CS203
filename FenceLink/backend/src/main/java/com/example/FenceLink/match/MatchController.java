package com.example.FenceLink.match;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{matchId}/{roundNo}/{tournamentId}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long matchId, @PathVariable int roundNo, @PathVariable Long tournamentId) {
        MatchId id = new MatchId(matchId, roundNo, tournamentId);
        Optional<Match> match = matchService.getMatchById(id);
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

    @PutMapping("/{matchId}/{roundNo}/{tournamentId}")
    public ResponseEntity<Match> updateMatch(@PathVariable Long matchId, @PathVariable int roundNo, @PathVariable Long tournamentId, @RequestBody Match matchDetails) {
        MatchId id = new MatchId(matchId, roundNo, tournamentId);
        Optional<Match> match = matchService.getMatchById(id);

        if (match.isPresent()) {
            Match updatedMatch = match.get();
            updatedMatch.setPlayer1Id(matchDetails.getPlayer1Id());
            updatedMatch.setPlayer2Id(matchDetails.getPlayer2Id());
            updatedMatch.setDate(matchDetails.getDate());
            updatedMatch.setStartTime(matchDetails.getStartTime());
            updatedMatch.setEndTime(matchDetails.getEndTime());
            updatedMatch.setWinner(matchDetails.getWinner());

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
