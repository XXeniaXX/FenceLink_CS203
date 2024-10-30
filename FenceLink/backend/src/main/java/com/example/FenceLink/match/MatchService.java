package com.example.FenceLink.match;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(Long matchId) {
        return matchRepository.findById(matchId);
    }

    public Optional<Tournament> findTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId);
    }

    @Transactional
    public Match createMatch(Match match) {
        if (match == null) {
            throw new IllegalArgumentException("Match cannot be null.");
        }
        if (match.getTournamentId() == null) {
            throw new IllegalArgumentException("Tournament ID is required but is missing.");
        }

        logger.info("Attempting to save Match with tournament ID: {}", match.getTournamentId());

        Tournament tournament = tournamentRepository.findById(match.getTournamentId())
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found for ID: " + match.getTournamentId()));

        match.setTournament(tournament);
        return matchRepository.save(match);
    }

    public Match updateMatch(Match match) {
        return matchRepository.save(match);
    }

    public void deleteMatch(Long matchId) {
        matchRepository.deleteById(matchId);
    }
}