package com.example.FenceLink.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    
    // Custom query to find matches by tournament ID
    List<Match> findByTournamentId(Long tournamentId);
    List<Match> findByTournamentIdAndRoundNo(Long tournamentId, int roundNo);

    @Query("SELECT MAX(m.roundNo) FROM Match m WHERE m.tournamentId = :tournamentId")
    Integer findMaxRoundByTournamentId(@Param("tournamentId") Long tournamentId);

}
