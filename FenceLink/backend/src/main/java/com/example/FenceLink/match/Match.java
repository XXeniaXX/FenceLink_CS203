package com.example.FenceLink.match;

import java.sql.Time;
import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "matches")
@IdClass(MatchId.class) // Define the composite key class
@NoArgsConstructor
public class Match {

    @Id
    @Column(name = "matchId")
    private Long matchId;

    @Id
    @Column(name = "roundNo")
    private int roundNo; 

    @JoinColumn(name = "Player1Id")
    private Long player1Id; 

    @JoinColumn(name = "Player2Id")
    private Long player2Id;

    @Temporal(TemporalType.DATE)
    @Column(name = "Date")
    private Date date;

    @Temporal(TemporalType.TIME)
    @Column(name = "StartTime")
    private Time startTime;

    @Temporal(TemporalType.TIME)
    @Column(name = "EndTime")
    private Time endTime;

    @Column(name = "Player1Points")
    private String player1points;

    @Column(name = "Player2Points")
    private String player2points;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "leaderboard_id")
    private Leaderboard leaderboard;

    // Constructor
    public Match(Long matchId, int roundNo, Long tournamentId, Long player1Id, Long player2Id, Date date, Time startTime, Time endTime, String player1points, String player2points) {
        this.matchId = matchId;
        this.roundNo = roundNo;
        this.tournamentId = tournamentId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.player1points = player1points;
        this.player2points = player2points;
        this. leaderboard = new Leaderboard();
    }
}
