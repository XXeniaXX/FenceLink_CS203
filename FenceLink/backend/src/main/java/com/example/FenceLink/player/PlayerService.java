package com.example.FenceLink.player;

import java.util.*;

import com.example.FenceLink.tournament.Tournament;

public interface PlayerService {
    Player findById(Long id);
    List<Player> findAll();
    Player insertPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    void deletePlayerById(Long id);
    List<Tournament> findUpcomingTournaments(Long playerId);
    List<Tournament> findUpcomingTournamentsForPlayer(Long playerId);
}
