package com.example.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class PlayerRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String InsertPlayer
    = "INSERT INTO players (id, name, gender, country, birthdate, location, fencing_weapon, bio, wins, losses, points, ranking) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SelectAllPlayers
    = "SELECT * FROM players";
    private static final String SelectPlayerById
    = "SELECT * FROM players WHERE id = ?";
    private static final String UpdatePlayer
    = "UPDATE players SET id = ?, name = ?, gender = ?, username = ?, country = ?, birthdate = ?, location = ?, fencing_weapon = ?, bio = ?, wins = ?, losses = ?, points = ?, ranking = ? WHERE id = ?";
    private static final String DeletePlayer
    = "DELETE FROM players WHERE id = ?";

    private RowMapper<Player> playerRowMapper = new RowMapper<Player>() {
        
        @Override
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            Player player = new Player();
            player.setId(rs.getString("id"));
            player.setName(rs.getString("name"));
            player.setGender(rs.getString("gender"));
            player.setCountry(rs.getString("country"));
            player.setBirthdate(rs.getDate("birthdate").toLocalDate());
            player.setLocation(rs.getString("location"));
            player.setFencingWeapon(rs.getString("fencing_weapon"));
            player.setBio(rs.getString("bio"));
            player.setWins(rs.getInt("wins"));
            player.setLosses(rs.getInt("losses"));
            player.setPoints(rs.getInt("points"));
            player.setRanking(rs.getInt("ranking"));
            return player;
        }
    };

    // Create new player
    public int addPlayer(Player player) {
        return jdbcTemplate.update(InsertPlayer, player.getId(), player.getName(),
        player.getGender(), player.getCountry(), player.getBirthdate(), player.getLocation(),
        player.getFencingWeapon(), player.getBio(), player.getWins(), player.getLosses(),
        player.getPoints(), player.getRanking());
    }

    // Get all players
    public List<Player> findAll() {
        return jdbcTemplate.query(SelectAllPlayers, playerRowMapper);
    }

    // Find player by ID
    public Optional<Player> findById(String id) {
        return jdbcTemplate.query(SelectPlayerById, playerRowMapper, id)
                .stream()
                .findFirst();
    }

    // Update a player's details
    public int updatePlayer(Player player) {
        return jdbcTemplate.update(UpdatePlayer, player.getId(), player.getName(),
        player.getGender(), player.getCountry(), player.getBirthdate(), player.getLocation(),
        player.getFencingWeapon(), player.getBio(), player.getWins(), player.getLosses(),
        player.getPoints(), player.getRanking(), player.getId());
    }

    // Delete player by their ID
    public int deletePlayer(String id) {
        return jdbcTemplate.update(DeletePlayer, id);
    }
}

