package com.example.FenceLink.match;

import java.sql.Time;
import java.util.Date;

import jakarta.persistence.*;
import lombok.*;
import com.example.FenceLink.leaderboard.Leaderboard;
import com.example.FenceLink.player.Player;
import com.example.FenceLink.tournament.Tournament;

@Data
@Entity
@Table(name = "matches")
@NoArgsConstructor
public class Match {

    @EmbeddedId
    private MatchId id;

    @MapsId("tournamentId")
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player player2;

    @Temporal(TemporalType.DATE)
    @Column(name = "Date")
    private Date date;

    @Temporal(TemporalType.TIME)
    @Column(name = "StartTime")
    private Time startTime;

    @Temporal(TemporalType.TIME)
    @Column(name = "EndTime")
    private Time endTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "leaderboard_id")
    private Leaderboard leaderboard;

    // Constructor
    public Match(MatchId id, Tournament tournament, Date date, Time startTime, Time endTime, Player player1, Player player2,
    Leaderboard leaderboard) {
        this.id = id;
        this.tournament = tournament;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.player1 = player1;
        this.player2 = player2;
        this. leaderboard = leaderboard;
    }
}
