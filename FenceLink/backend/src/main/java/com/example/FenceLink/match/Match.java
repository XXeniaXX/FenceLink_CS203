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

    @Id
    @Column(name = "TournamentID")
    private Long tournamentId;

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

    @Column(name = "Winner")
    private String winner;

    // Constructor
    public Match(Long matchId, int roundNo, Long tournamentId, Long player1Id, Long player2Id, Date date, Time startTime, Time endTime, String winner) {
        this.matchId = matchId;
        this.roundNo = roundNo;
        this.tournamentId = tournamentId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.winner = winner;
    }
}
