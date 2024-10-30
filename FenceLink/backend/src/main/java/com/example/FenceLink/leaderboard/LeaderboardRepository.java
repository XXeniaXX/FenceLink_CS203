package com.example.FenceLink.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.match.Match;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    Optional<Leaderboard> findByTournament(Tournament tournament);
    Optional<Leaderboard> findByMatch(Match match);
}
