package com.example.FenceLink.match;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(MatchId id) {
        return matchRepository.findById(id);
    }

    public List<Match> getMatchesByTournament(Tournament tournament) {
        return matchRepository.findByTournament(tournament);
    }

    public Optional<Tournament> findTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId);
    }

    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }

    public Match updateMatch(Match match) {
        return matchRepository.save(match);
    }

    public void deleteMatch(MatchId id) {
        matchRepository.deleteById(id);
    }
}

