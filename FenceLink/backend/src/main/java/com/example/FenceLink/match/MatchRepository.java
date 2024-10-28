package com.example.FenceLink.match;

import com.example.FenceLink.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, MatchId> {
    List<Match> findByTournament(Tournament tournament);
}

