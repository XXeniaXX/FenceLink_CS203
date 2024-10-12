package com.example.FenceLink.player;

import jakarta.persistence.*;
import java.time.*;

import org.hibernate.annotations.UuidGenerator;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "player")
@NoArgsConstructor
public class Player {
    
    public Player(String id, String name, String gender, String country, LocalDate birthdate, String location,
            String fencingWeapon, String bio, int wins, int losses) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.country = country;
        this.birthdate = birthdate;
        this.location = location;
        this.fencingWeapon = fencingWeapon;
        this.bio = bio;
        this.wins = wins;
        this.losses = losses;
    }

    @Id
    @UuidGenerator
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

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

    @Column(name = "wins")
    private int wins;

    @Column(name = "losses")
    private int losses;
    // private int points;
    // private int ranking;
    
}
