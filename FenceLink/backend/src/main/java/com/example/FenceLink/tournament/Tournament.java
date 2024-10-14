package com.example.FenceLink.tournament;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


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
    @Column(name = "date") // Specify the column name and constraints
    private Date date;

    public Tournament(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}


