package com.example.FenceLink.player;

import java.time.LocalDate;
import java.util.*;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.UpcomingTournamentResponse;

public interface PlayerService {
    Player findById(Long id);
    Player findByUserId(Long userId);
    List<Player> findAll();
    Player insertPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    void deletePlayerById(Long id);
    List<UpcomingTournamentResponse> findUpcomingTournaments(Long playerId);
    List<Tournament> findUpcomingRegisteredTournaments(Long playerId);
    int calculateAge(LocalDate birthdate);
    
}
