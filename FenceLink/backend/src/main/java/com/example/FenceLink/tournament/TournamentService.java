package com.example.FenceLink.tournament;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    public void addTournament(Tournament tournament) {
        tournamentRepository.addTournament(tournament);
    }

    public void updateTournament(Tournament tournament) {
        tournamentRepository.updateTournament(tournament);
    }

    public void deleteTournament(Long id) {
        tournamentRepository.deleteTournament(id);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.getAllTournaments();
    }

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.getTournamentById(id);
    }
}