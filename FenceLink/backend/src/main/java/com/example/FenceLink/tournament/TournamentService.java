package com.example.FenceLink.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    private static final Logger logger = Logger.getLogger(TournamentService.class.getName());

    // Add or update tournament
    public Tournament addTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    // Update tournament
    public Tournament updateTournament(Tournament tournament) {
        Optional<Tournament> existingTournament = tournamentRepository.findById(tournament.getId());
        if (existingTournament.isPresent()) {
            return tournamentRepository.save(tournament);
        } else {
            throw new RuntimeException("Tournament not found for id: " + tournament.getId());
        }
    }

    //Delete Tournament by ID 
    public void deleteTournament(Long id) {
        tournamentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tournament not found for id: " + id));
        tournamentRepository.deleteById(id);
    }



    // Get all tournaments
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    // Get tournament by ID
    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Tournament not found for id: " + id)
        );
    }


     // Get tournaments by specfified parameters (For Search and Filter)
    public List<Tournament> searchTournaments(
        String name,
        String location,
        String tournamentType,
        String genderType,
        String weaponType,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            logger.warning("Invalid date range: fromDate is after toDate");
            return List.of();
        }

        return tournamentRepository.searchTournaments(
            name, location, tournamentType, genderType, weaponType, fromDate, toDate);
    }
}

