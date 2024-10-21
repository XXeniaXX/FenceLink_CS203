package com.example.FenceLink.player;

import jakarta.persistence.*;
import java.time.*;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import com.example.FenceLink.tournament.*;


@Data
@Builder
@Entity
@Table(name = "player")
@NoArgsConstructor
public class Player {
    
    public Player(Long id, String name, String gender, String country, LocalDate birthdate, String location,
            String fencingWeapon, String bio, Integer points, List<Tournament> tournamentsRegistered) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.country = country;
        this.birthdate = birthdate;
        this.location = location;
        this.fencingWeapon = fencingWeapon;
        this.bio = bio;
        this.points = points;
        this.tournamentsRegistered = tournamentsRegistered;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "country")
    private String country;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "location")
    private String location;

    @Column(name = "fencingWeapon")
    private String fencingWeapon;

    @Column(name = "bio")
    private String bio;

    @Column(name = "points")
    private int points;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinTable(
        name = "tournament_registered",
        joinColumns = @JoinColumn(name = "player_id",referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tournament_id",referencedColumnName = "id")
    )
    private List<Tournament> tournamentsRegistered;

    
    
}
