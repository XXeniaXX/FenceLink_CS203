package com.example.FenceLink.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.tournament.TournamentRepository;
import com.example.FenceLink.tournament.UpcomingTournamentResponse;

import jakarta.transaction.Transactional;

import java.time.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import com.example.FenceLink.user.*;



@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

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
    public Player findByUserId(Long userId) {
        return playerRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Player not found for userId: " + userId));
    }

    @Override
    public int calculateAge(LocalDate birthdate) {
        if (birthdate == null) {
            throw new IllegalArgumentException("Birthdate is required to calculate age");
        }
        return Period.between(birthdate, LocalDate.now()).getYears();
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
        Player existingPlayer = playerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Player with ID: " + id + " not found!"));

        checkPlayer(updatedPlayer);

        // Attach the existing User instance if provided in the updated data
        if (updatedPlayer.getUser() != null && updatedPlayer.getUser().getId() != null) {
            User existingUser = userRepository.findById(updatedPlayer.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            existingPlayer.setUser(existingUser); // Attach the existing User instance to existingPlayer
        }

        // Update fields in existingPlayer
        existingPlayer.setName(updatedPlayer.getName());
        existingPlayer.setGender(updatedPlayer.getGender());
        existingPlayer.setLocation(updatedPlayer.getLocation());
        existingPlayer.setCountry(updatedPlayer.getCountry());
        existingPlayer.setFencingWeapon(updatedPlayer.getFencingWeapon());
        existingPlayer.setBirthdate(updatedPlayer.getBirthdate());
        existingPlayer.setBio(updatedPlayer.getBio());

        // Save the updated player
        playerRepository.saveAndFlush(existingPlayer);

        // Return the updated existingPlayer, not updatedPlayer
        return existingPlayer;
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
        if ((ageGroup.equalsIgnoreCase("Youth") && age.getYears() >= 18) || 
        (ageGroup.equalsIgnoreCase("Adult") && age.getYears() < 18)) {
            throw new IllegalArgumentException("Player's age does not meet the tournament's age requirement!");
        }

        // Check if the player's gender matches the tournament's gender type or if the tournament is "Mixed"
        if (!tournament.getGenderType().equalsIgnoreCase("Mixed") && 
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

    // Method to check if a player is eligible for the tournament based on gender
    private boolean isPlayerEligibleForGender(Player player, String genderType) {
        // If the tournament is "Mixed", any player can participate
        if (genderType.equals("Mixed")) {
            return true;
        }
        // Otherwise, the player must match the gender type of the tournament
        return genderType.equals(player.getGender());
    }

    // Method to check if player's age fits into the tournament's age group
    private boolean isPlayerEligibleForAgeGroup(int playerAge, String ageGroup) {
        switch (ageGroup) {
            case "Youth":
                return playerAge < 18;
            case "Adult":
                return playerAge >= 18;
            default:
                return true;  // If no specific age group, assume eligible
        }
    }

    // Method for checking if the player has a clashing tournament
    public boolean hasClashingTournament(Player player, Tournament newTournament) {
        return player.getTournamentsRegistered().stream()
            .anyMatch(registeredTournament -> 
                !registeredTournament.getEndDate().isBefore(newTournament.getStartDate()) && 
                !registeredTournament.getStartDate().isAfter(newTournament.getEndDate())
            );
    }

    // Method to get a list of upcoming tournaments that player can register for
    @Override
    public List<UpcomingTournamentResponse> findUpcomingTournaments(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );

        List<Tournament> registeredTournaments = player.getTournamentsRegistered();
        
        LocalDate today = LocalDate.now();
        int playerAge = Period.between(player.getBirthdate(), LocalDate.now()).getYears();

        // Fetch all tournaments and filter based on the current date, exclude the ones the player is already registered for
        return tournamentRepository.findAll().stream()
                .filter(tournament -> !tournament.getRegistrationDate().isBefore(today) && 
                                      !registeredTournaments.contains(tournament) &&
                                      isPlayerEligibleForGender(player, tournament.getGenderType()) &&
                                      isPlayerEligibleForAgeGroup(playerAge, tournament.getAgeGroup()) //&&
                                      //player.getFencingWeapon().equals(tournament.getCategory())
                                      )
                .map(tournament -> {
                    boolean hasClash = hasClashingTournament(player, tournament);
                    String clashMessage = hasClash ? "This tournament clashes with another tournament you are registered for." : null;
                    return new UpcomingTournamentResponse(tournament, clashMessage);
                    }) // returns clash message abt whether player have a registered clashing tournament
                .collect(Collectors.toList());
    }

    

    // Method for a Player to get upcoming tournaments that they have registered for
    @Override
    public List<Tournament> findUpcomingRegisteredTournaments(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );

        LocalDate today = LocalDate.now();

        // Filter tournaments that are scheduled after today
        return player.getTournamentsRegistered().stream()
                .filter(tournament -> tournament.getStartDate().isAfter(today))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tournament> findPastRegisteredTournaments(Long playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> 
            new IllegalArgumentException("Player with ID " + playerId + " not found!")
        );

        LocalDate today = LocalDate.now();

        // Filter tournaments that have ended before today
        return player.getTournamentsRegistered().stream()
                .filter(tournament -> tournament.getEndDate().isBefore(today))
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

    public Page<PlayerDTO> getTopPlayersPage(int page, int pageSize, String gender, String country) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("points").descending());
    
        Page<Player> players;
        
        // Apply filters only if they are provided
        if (gender != null && !gender.isEmpty() && country != null && !country.isEmpty()) {
            players = playerRepository.findByGenderAndCountry(gender, country, pageRequest);
        } else if (gender != null && !gender.isEmpty()) {
            players = playerRepository.findByGender(gender, pageRequest);
        } else if (country != null && !country.isEmpty()) {
            players = playerRepository.findByCountry(country, pageRequest);
        } else {
            players = playerRepository.findAll(pageRequest);
        }
    
        // Convert Page<Player> to Page<PlayerDTO>
        return players.map(this::convertToPlayerDTO);
    }
    
    // Conversion method to map Player to PlayerDTO
    private PlayerDTO convertToPlayerDTO(Player player) {
        return new PlayerDTO(
            player.getId(),
            player.getName(),
            player.getGender(),
            player.getCountry(),
            player.getPoints()
            // Add other fields if necessary
        );
    }

    public List<String> getAllCountries() {
        return playerRepository.findDistinctCountries();
    }
}
