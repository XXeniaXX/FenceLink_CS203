package com.example.FenceLink.player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    Optional<Player> findById(String id);
    List<Player> findAll();
    void deletePlayerById(String id);
}

