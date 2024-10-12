package com.example.FenceLink.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    // No need to define standard CRUD methods like save, delete, findAll, findById
    // Spring Data JPA automatically provides them

    // If you want custom queries, you can use @Query annotation here
    // Example: Fetch all tournaments by name
    List<Tournament> findByName(String name);
}
