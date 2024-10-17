package com.example.FenceLink.tournament;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import com.example.FenceLink.player.Player;
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

    @Temporal(TemporalType.DATE) // Specify the type of the date
    @Column(name = "RegistrationDate") // Specify the column name and constraints
    private Date RegistrationDate;

    @Column(name = "description")
    private String description;

    @Column(name = "tournamentType")
    private String tournamentType;

    @Column(name = "category")
    private String category;

    @Column(name = "genderType")
    private String genderType;

    @Column(name = "ageGroup")
    private String ageGroup;

    @Temporal(TemporalType.DATE) // Specify the type of the date
    @Column(name = "startDate") // Specify the column name and constraints
    private Date startDate;

    @Temporal(TemporalType.DATE) // Specify the type of the date
    @Column(name = "endDate") // Specify the column name and constraints
    private Date endDate;

    @Column(name = "vacancy") // Specify the column name and constraints
    private int vancany;

    public Tournament(String name, String location, Date RegistrationDate, String description, String tournamentType, 
                    String category, String genderType, String ageGroup, Date startDate, Date endDate, int vacancy) {
        this.name = name;
        this.location = location;
        this.RegistrationDate = RegistrationDate;
        this.description = description; 
        this.tournamentType = tournamentType;
        this.category = category; 
        this.genderType = genderType; 
        this.ageGroup = ageGroup; 
        this.startDate = startDate; 
        this.endDate = endDate; 
        this.vancany = vacancy;

    }
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

    public Date getRegistrationDate() {
        return RegistrationDate;
    }

    public void setDate(Date RegistrationDate) {
        this.RegistrationDate = RegistrationDate;
    }
}


