package com.example.FenceLink.player;

import java.util.*;

public interface PlayerService {
    Player findById(String id);
    List<Player> findAll();
    Player insertPlayer(Player player);
    Player updatePlayer(String id, Player player);
    void deletePlayerById(String id);
}
