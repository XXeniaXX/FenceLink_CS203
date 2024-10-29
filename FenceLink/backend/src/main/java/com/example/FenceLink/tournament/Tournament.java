package com.example.FenceLink.tournament;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.FenceLink.player.Player;
import com.example.FenceLink.match.Match;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "tournaments")
@NoArgsConstructor
public class Tournament {
    
    @Id // Specifies the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the ID
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "location") // Specify the column name and constraints
    private String location;


    @Column(name = "RegistrationDate") // Specify the column name and constraints
    private LocalDate RegistrationDate;

    @Column(name = "description")
    private String description;

    @Column(name = "tournamentType")
    private String tournamentType;

    @Column(name = "weaponType")
    private String weaponType;

    @Column(name = "genderType")// "Male", "Female", or "Open" for mixed tournaments
    private String genderType;

    @Column(name = "ageGroup")//"Teen":<18 years old, "Adult">=18 years old
    private String ageGroup;

    @Column(name = "startDate") // Specify the column name and constraints
    private LocalDate startDate;

    @Column(name = "endDate") // Specify the column name and constraints
    private LocalDate endDate;

    @Column(name = "vacancy") // Specify the column name and constraints
    private int vacancy;

    public Tournament(String name, String location, LocalDate RegistrationDate, String description, String tournamentType, 
                    String weaponType, String genderType, String ageGroup, LocalDate startDate, LocalDate endDate, int vacancy) {
        this.name = name;
        this.location = location;
        this.RegistrationDate = RegistrationDate;
        this.description = description; 
        this.tournamentType = tournamentType;
        this.weaponType = weaponType; 
        this.genderType = genderType; 
        this.ageGroup = ageGroup; 
        this.startDate = startDate; 
        this.endDate = endDate; 
        this.vacancy = vacancy;

    }
    @OneToMany(mappedBy = "tournament")
    private List<Match> matches;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(name = "tournament_registered", joinColumns = @JoinColumn(name = "tournament_id",referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "player_id",referencedColumnName = "id"))
    @JsonIgnore  // Prevent infinite recursion
    private List<Player> players;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getRegistrationDate() {
        return RegistrationDate;
    }

    public void setRegistrationDate(LocalDate RegistrationDate) {
        this.RegistrationDate = RegistrationDate;
    }

    public String getAgeGroup() {
        return ageGroup;
    }
    
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getGenderType() {
        return genderType;
    }
    
    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    public int getVacancy() {
        return vacancy;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }

}


