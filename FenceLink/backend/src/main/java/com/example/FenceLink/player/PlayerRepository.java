package com.example.FenceLink.player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(Long id);
    Optional<Player> findByUserId(Long userId);
    List<Player> findAll();
    void deletePlayerById(Long id);
}

