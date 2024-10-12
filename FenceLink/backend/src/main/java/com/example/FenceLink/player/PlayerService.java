package com.example.FenceLink.player;

import java.util.*;

public interface PlayerService {
    Player findById(Long id);
    List<Player> findAll();
    Player insertPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    void deletePlayerById(Long id);
}
