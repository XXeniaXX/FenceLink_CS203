package com.example.FenceLink.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    // No need to define standard CRUD methods like save, delete, findAll, findById
    // Spring Data JPA automatically provides them
    // If you want custom queries, you can use @Query annotation here
    // Example: Fetch all tournaments by name
    List<Tournament> findByName(String name);

    // For Search and Filter:
    // Partial and case-insensitive search by name and location
    // Filters based on tournamentType, genderType, weaponType, startDate, and endDate
    // If any parameter is null, it is ignored in the search criteria
    @Query("SELECT t FROM Tournament t " +
           "WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:location IS NULL OR LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:tournamentType IS NULL OR t.tournamentType = :tournamentType) " +
           "AND (:genderType IS NULL OR t.genderType = :genderType) " +
           "AND (:weaponType IS NULL OR t.weaponType = :weaponType) " +
           "AND (:fromDate IS NULL OR t.startDate >= :fromDate) " +
           "AND (:toDate IS NULL OR t.endDate <= :toDate)")
    List<Tournament> searchTournaments(
        @Param("name") String name,
        @Param("location") String location,
        @Param("tournamentType") String tournamentType,
        @Param("genderType") String genderType,
        @Param("weaponType") String weaponType,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );
}
