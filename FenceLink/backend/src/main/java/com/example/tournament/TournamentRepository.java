package com.example.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TournamentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Add tournament
    public void addTournament(Tournament tournament) {
        String sql = "INSERT INTO tournaments (name, location, date) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, tournament.getName(), tournament.getLocation(), tournament.getDate());
    }

    // Update tournament
    public void updateTournament(Tournament tournament) {
        String sql = "UPDATE tournaments SET name = ?, location = ?, date = ? WHERE id = ?";
        jdbcTemplate.update(sql, tournament.getName(), tournament.getLocation(), tournament.getDate(), tournament.getId());
    }

    // Delete tournament
    public void deleteTournament(Long id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Get all tournaments
    public List<Tournament> getAllTournaments() {
        String sql = "SELECT * FROM tournaments";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Tournament t = new Tournament();
            t.setId(rs.getString("id"));
            t.setName(rs.getString("name"));
            t.setLocation(rs.getString("location"));
            t.setDate(rs.getDate("date"));
            return t;
        });
    }
}
