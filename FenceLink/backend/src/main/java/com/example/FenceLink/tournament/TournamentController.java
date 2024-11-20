package com.example.FenceLink.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.Collections;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "http://localhost:3000")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;


    //Gets All Tournaments 
    @GetMapping
    public List<Tournament> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }

    //Gets all upcoming tournaments 
    @GetMapping("/upcomingtournaments")
    public List<Tournament> getAllUpcomingTournaments() {
        LocalDate today = LocalDate.now();
        return tournamentService.getAllTournaments()
                                .stream()
                                .filter(tournament -> tournament.getStartDate().isAfter(today))
                                .collect(Collectors.toList());
    }

    //Get a tournament by its ID 
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament != null) {
            return new ResponseEntity<>(tournament, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Add new tournament 
    @PostMapping
    public ResponseEntity<Tournament> addTournament(@RequestBody Tournament tournament) {
        Tournament createdTournament = tournamentService.addTournament(tournament);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }


    //Update a tournament based on its ID 
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

    //delete a tournament bases on its ID 
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
        if (tournamentService.getTournamentById(id) != null) {
            tournamentService.deleteTournament(id);
            return new ResponseEntity<>("Tournament deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Tournament not found", HttpStatus.NOT_FOUND);
        }
    }

    //Searches for tournaments based on optional filter citerias 
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


