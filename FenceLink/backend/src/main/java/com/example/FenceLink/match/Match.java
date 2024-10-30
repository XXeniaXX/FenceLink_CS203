package com.example.FenceLink.match;

import java.sql.Time;
import java.time.LocalDate;

import com.example.FenceLink.tournament.Tournament;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "matches")
@NoArgsConstructor
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;


    @Column(name = "roundNo")
    private int roundNo; 

    @Column(name = "Player1Id")
    private Long player1Id; 

    @Column(name = "Player2Id")
    private Long player2Id;

    @Column(name = "Date")
    private LocalDate date;

    @Temporal(TemporalType.TIME)
    @Column(name = "StartTime")
    private Time startTime;

    @Temporal(TemporalType.TIME)
    @Column(name = "EndTime")
    private Time endTime;

    @Column(name = "Player1Points")
    private int player1points;

    @Column(name = "Player2Points")
    private int player2points;

    @Column(name = "Winner")
    private Long winner;



    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tournament_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Tournament tournament;


    // Constructor
    public Match(Long matchId, int roundNo, Long tournamentId, Long player1Id, Long player2Id, LocalDate date, Time startTime, Time endTime, int player1points, int player2points, Long winner) {
        this.matchId = matchId;
        this.roundNo = roundNo;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.player1points = player1points;
        this.player2points = player2points;
    }
}
