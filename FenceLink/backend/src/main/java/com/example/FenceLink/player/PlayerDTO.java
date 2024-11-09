package com.example.FenceLink.player;

public class PlayerDTO {
    private Long id;
    private String name;
    private String gender;
    private String country;
    private int points;

    public PlayerDTO(Long id, String name, String gender, String country, int points) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.country = country;
        this.points = points;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
