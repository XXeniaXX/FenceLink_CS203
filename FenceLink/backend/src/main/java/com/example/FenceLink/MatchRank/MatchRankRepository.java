package com.example.FenceLink.MatchRank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.FenceLink.tournament.Tournament;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRankRepository extends JpaRepository<MatchRank, Long> {

    // Find all MatchRank entities by tournament ID
    List<MatchRank> findByTournamentId(Long tournamentId);

    // Find a MatchRank entity by tournament ID and player ID
    Optional<MatchRank> findByTournamentIdAndPlayerId(Long tournamentId, Long playerId);
    
}
