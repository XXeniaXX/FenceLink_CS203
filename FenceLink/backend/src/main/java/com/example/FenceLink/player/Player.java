package com.example.FenceLink.player;

import jakarta.persistence.Id;
import java.time.*;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document("player")
@Data
@Builder
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
    
}
