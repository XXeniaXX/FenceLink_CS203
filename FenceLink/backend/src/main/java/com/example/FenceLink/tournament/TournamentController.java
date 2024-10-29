package com.example.FenceLink.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.util.Collections;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament != null) {
            return new ResponseEntity<>(tournament, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Tournament> addTournament(@RequestBody Tournament tournament) {
        Tournament createdTournament = tournamentService.addTournament(tournament);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        if (tournamentService.getTournamentById(id) != null) {
            tournament.setId(id);
            tournamentService.updateTournament(tournament);
            return new ResponseEntity<>("Tournament updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Tournament not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        if (tournamentService.getTournamentById(id) != null) {
            tournamentService.deleteTournament(id);
            return new ResponseEntity<>("Tournament deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Tournament not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTournaments(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) String tournamentType,
        @RequestParam(required = false) String genderType,
        @RequestParam(required = false) String weaponType,
        @RequestParam(required = false) LocalDate fromDate,
        @RequestParam(required = false) LocalDate toDate
        ) {
        List<Tournament> tournaments = tournamentService.searchTournaments(
            name, location, tournamentType, genderType, weaponType, fromDate, toDate);
        
        if (tournaments.isEmpty()) {
            return new ResponseEntity<>(
                Collections.singletonMap("message", "No tournaments found matching the search criteria."), 
                HttpStatus.NOT_FOUND);


        }
        return ResponseEntity.ok(tournaments);
    }
}



