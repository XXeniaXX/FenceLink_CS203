package com.example.player;

import java.time.LocalDate;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
