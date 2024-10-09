package com.example.FenceLink.player;

import jakarta.persistence.Id;
import java.time.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("player")
public class Player {
    
    @Id
    private String id;

    private String name;
    private String gender;
    private String country;
    private LocalDate birthdate;
    private String location;
    private String fencingWeapon;
    private String bio;

    private int wins;
    private int losses;
    private int points;
    private int ranking;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public LocalDate getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getFencingWeapon() {
        return fencingWeapon;
    }
    public void setFencingWeapon(String fencingWeapon) {
        this.fencingWeapon = fencingWeapon;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public int getWins() {
        return wins;
    }
    public void setWins(int wins) {
        this.wins = wins;
    }
    public int getLosses() {
        return losses;
    }
    public void setLosses(int losses) {
        this.losses = losses;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public int getRanking() {
        return ranking;
    }
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    
}
