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
    
    // Used to record log messages (info, debug,warning, error)
    private static final Logger logger = Logger.getLogger(TournamentService.class.getName());

    // Add or update tournament (JPA's save method handles both)
    public Tournament addTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    // Update tournament
    public Tournament updateTournament(Tournament tournament) {
        // Ensure that the tournament exists before updating
        Optional<Tournament> existingTournament = tournamentRepository.findById(tournament.getId());
        if (existingTournament.isPresent()) {
            return tournamentRepository.save(tournament);  // JPA's save method also updates
        } else {
            throw new RuntimeException("Tournament not found for id: " + tournament.getId());
        }
    }

    // Delete tournament by ID
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
    // calls the repository’s custom query
    public List<Tournament> searchTournaments(
        String name,
        String location,
        String tournamentType,
        String genderType,
        String weaponType,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        // Handle special case for invalid date range
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            logger.warning("Invalid date range: fromDate is after toDate");
            return List.of(); // return empty result
        }

        return tournamentRepository.searchTournaments(
            name, location, tournamentType, genderType, weaponType, fromDate, toDate);
    }
}
