package com.example.FenceLink.player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(Long id);
    Optional<Player> findByUserId(Long userId);
    List<Player> findAll();
    void deletePlayerById(Long id);
    Page<Player> findByGender(String gender, Pageable pageable);
    Page<Player> findByCountry(String country, Pageable pageable);
    Page<Player> findByGenderAndCountry(String gender, String country, Pageable pageable);
  
    @Query("SELECT DISTINCT p.country FROM Player p WHERE p.country IS NOT NULL")
    List<String> findDistinctCountries();
}

