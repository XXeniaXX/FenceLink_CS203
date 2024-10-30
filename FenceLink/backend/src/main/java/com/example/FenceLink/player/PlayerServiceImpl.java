package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;

import jakarta.transaction.Transactional;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    //new for join table
    @Autowired
    private TournamentRepository tournamentRepository;

    // Check player info validity
    public void checkPlayer(Player player) throws IllegalArgumentException {
        // Name cannot be empty
        if (player.getName() == null || player.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        LocalDate currentDate = LocalDate.now();
        int age = Period.between(player.getBirthdate(), currentDate).getYears();

        // Birthdate should not be in the future, should be >= 14?
        if (player.getBirthdate().isAfter(currentDate)) {
            throw new IllegalArgumentException("Birthdate cannot be in the future!");
        }

        if (age < 14) {
            throw new IllegalArgumentException("Player must be at least 14 years old!");
        }

        // Points cannot be negative
        if (player.getPoints() < 0) {
            throw new IllegalArgumentException("Data Invalid: points cannot be negative!");
        }
    }

    @Override
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    @Override
    public Player findById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Player insertPlayer(Player player) throws IllegalArgumentException {
        // Points 0 since new player hasn't joined anything
        player.setPoints(0);

        checkPlayer(player);

        Player.PlayerBuilder playerBuilder = Player.builder()
                .name(player.getName())
                .birthdate(player.getBirthdate())
                .points(player.getPoints());

        if (player.getGender() != null) {
            playerBuilder.bio(player.getGender());
        }

        if (player.getBio() != null) {
            playerBuilder.bio(player.getBio());
        }

        if (player.getCountry() != null) {
            playerBuilder.birthdate(player.getBirthdate());
        }

        if (player.getFencingWeapon() != null) {
            playerBuilder.fencingWeapon(player.getFencingWeapon());
        }

        if (player.getLocation() != null) {
            playerBuilder.location(player.getLocation());
        }

        if (player.getTournamentsRegistered() != null) {
            playerBuilder.tournamentsRegistered(player.getTournamentsRegistered());
        }

        playerBuilder.build();

        playerRepository.saveAndFlush(player);
        return player;
    }

    // Admin only
    @Override
    @Transactional
    public Player updatePlayer(Long id, Player updatedPlayer) throws IllegalArgumentException {
        // Ensures player actually exists
        if (!playerExists(id)) {
            throw new IllegalArgumentException("Player with ID: " + id + " not found!");
        }

        checkPlayer(updatedPlayer);

        // Id cannot be empty
        if (updatedPlayer.getId() == null) {
            throw new IllegalArgumentException("Player ID is required!");
        }

        playerRepository.saveAndFlush(updatedPlayer);
        return updatedPlayer;
    }

    // Admin only
    @Override
    @Transactional
    public void deletePlayerById(Long id) throws IllegalArgumentException {
        // Ensures player actually exists
        if (!playerExists(id)) {
            throw new IllegalArgumentException("Player not found!");
        }
        playerRepository.deletePlayerById(id);
    }

    public boolean playerExists(Long id) throws IllegalArgumentException {
        return playerRepository.findById(id).isPresent();
    }

    // Method to register a player for a tournament
    @Transactional
    public String registerPlayerForTournament(Long playerId, Long tournamentId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> 
            new IllegalArgumentException("Tournament with ID " + tournamentId + " not found!")
        );

        // check if player already registered for the tournament
        if (player.getTournamentsRegistered().contains(tournament)) {
            throw new IllegalArgumentException("Player already registered for the tournament!");
        }

        // Check if current date is before the tournament's registration deadline
        LocalDate currentDate = LocalDate.now();
        LocalDate registrationDate = tournament.getRegistrationDate();
        if (currentDate.isAfter(registrationDate)) {
            throw new IllegalArgumentException("Registration deadline for " + tournament.getName() + " has passed!");
        }

        // Check if there are vacancies left for the tournament
        if (tournament.getVacancy() <= 0) {
            throw new IllegalArgumentException("No vacancies left for " + tournament.getName() + "!");
        }

        // Calculate the player's age based on their birthdate
        Period age = Period.between(player.getBirthdate(), currentDate);

        // Check if the player's age fits the tournament's age group
        String ageGroup = tournament.getAgeGroup(); // Assuming the age group is stored in the tournament object
        if ((ageGroup.equals("Teen") && age.getYears() >= 18) || 
        (ageGroup.equals("Adults") && age.getYears() < 18)) {
            throw new IllegalArgumentException("Player's age does not meets the tournament's age requirement!");
        }

        // Check if the player's gender matches the tournament's gender type or if the tournament is "Open"
        if (!tournament.getGenderType().equalsIgnoreCase("Open") && 
        !tournament.getGenderType().equalsIgnoreCase(player.getGender())) {
            throw new IllegalArgumentException("Player's gender does not match the tournament's gender requirement!");
        }

        // If all checks pass, register the player and update the tournament's vacancy
        player.getTournamentsRegistered().add(tournament);
        playerRepository.save(player);  // Save updated player
        tournament.setVacancy(tournament.getVacancy() - 1);//update vacancy
        tournamentRepository.save(tournament);  // Save updated tournament
        
        // Return success message
        return player.getName() + " successfully registered for " + tournament.getName() + ".";
    }

    // Method to withdraw player from tournament
    @Transactional
    public String withdrawPlayerFromTournament(Long playerId, Long tournamentId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> 
            new IllegalArgumentException("Tournament with ID " + tournamentId + " not found!")
        );

        // Remove the tournament from the player's registered list if present
        if (player.getTournamentsRegistered().contains(tournament)) {
            player.getTournamentsRegistered().remove(tournament);
            playerRepository.save(player);  // Save updated player
            return player.getName() + " successfully withdrawn from " + tournament.getName() + ".";
        } else {
            throw new IllegalArgumentException(player.getName() + " is not registered for " + tournament.getName() + ".");
        }
    }

    // Method to get a list of upcoming tournaments that player can register for
    @Override
    public List<Tournament> findUpcomingTournaments(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );

        List<Tournament> registeredTournaments = player.getTournamentsRegistered();
        
        LocalDate today = LocalDate.now();

        // Fetch all tournaments and filter based on the current date, exclude the ones the player is already registered for
        return tournamentRepository.findAll().stream()
                .filter(tournament -> !tournament.getRegistrationDate().isBefore(today) && 
                                    !registeredTournaments.contains(tournament))
                .collect(Collectors.toList());
    }

    // Method for a Player to get upcoming tournaments that they have registered for
    @Override
    public List<Tournament> findUpcomingTournamentsForPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );

        LocalDate today = LocalDate.now();

        // Filter tournaments that are scheduled after today
        return player.getTournamentsRegistered().stream()
                .filter(tournament -> tournament.getRegistrationDate().isAfter(today))
                .collect(Collectors.toList());
    }
    //get player's id who has register for a specific tournament
    public List<Long> getRegisteredPlayerIds(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new IllegalArgumentException("Tournament with ID " + tournamentId + " not found!"));
        return tournament.getPlayers().stream()
            .map(Player::getId)
            .collect(Collectors.toList());
    }

}
