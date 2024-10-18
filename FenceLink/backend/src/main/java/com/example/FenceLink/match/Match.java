package com.example.FenceLink.match;

import java.sql.Time;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

@Data
@Entity
@Table(name = "match")
@NoArgsConstructor
public class Match {
    
    @Id // Specifies the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate the ID
    @Column(name = "matchId")
    private Long matchId;

    @Id
    @Column(name = "roundNo")
    private int roundNo; 

    @Id
    @Column(name = "TournamentID")
    private Long tournamentId;

    @JoinColumn(name = "Player1Id" )
    private Long player1Id; 

    @JoinColumn(name = "Player2Id")
    private Long player2Id;

    @Temporal(TemporalType.DATE) // Specify the type of the date
    @Column(name = "Date") // Specify the column name and constraints
    private Date date;

    @Temporal(TemporalType.TIME) // Specify the type of the date
    @Column(name = "Date") // Specify the column name and constraints
    private Time startTime;

    @Temporal(TemporalType.TIME)
    @Column(name = "EndTime") 
    private Time endTime;

    @Column (name = "Winner")
    private String winner; 

    public Match(Long matchId, int roundNo, Long tournamentId, Long player1Id, Long player2Id ,Date date, Time startTime, Time endTime, String winner){
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
