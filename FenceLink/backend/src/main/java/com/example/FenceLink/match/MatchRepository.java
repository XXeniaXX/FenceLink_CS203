package com.example.FenceLink.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    
    // Custom query to find matches by tournament ID
    List<Match> findByTournamentId(Long tournamentId);
}
