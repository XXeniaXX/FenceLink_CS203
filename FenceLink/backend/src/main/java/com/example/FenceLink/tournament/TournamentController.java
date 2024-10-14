package com.example.FenceLink.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    // Get tournament by ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament != null) {
            return new ResponseEntity<>(tournament, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Add a new tournament
    @PostMapping
    public ResponseEntity<String> addTournament(@RequestBody Tournament tournament) {
        tournamentService.addTournament(tournament);
        return new ResponseEntity<>("Tournament added successfully", HttpStatus.CREATED);
    }

    // Update a tournament
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        tournament.setId(id);
        tournamentService.updateTournament(tournament);
        return new ResponseEntity<>("Tournament updated successfully", HttpStatus.OK);
    }

    // Delete a tournament
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return new ResponseEntity<>("Tournament deleted successfully", HttpStatus.OK);
    }
}


