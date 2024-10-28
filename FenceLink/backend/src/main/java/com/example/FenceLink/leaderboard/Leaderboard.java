package com.example.FenceLink.leaderboard;

import com.example.FenceLink.tournament.Tournament;
import com.example.FenceLink.match.Match;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "leaderboard")
    private Tournament tournament;

    @OneToOne(mappedBy = "leaderboard")
    private Match match;

    public Leaderboard() {}

}
