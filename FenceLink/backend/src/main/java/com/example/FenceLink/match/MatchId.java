package com.example.FenceLink.match;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;

public class MatchId implements Serializable {

    @Column(name = "matchId")
    private Long matchId;

    @Column(name = "roundNo")
    private int roundNo;

    @Column(name = "TournamentID")
    private Long tournamentId;

    // Default constructor (no-arg)
    public MatchId() {
    }

    // Parameterized constructor
    public MatchId(Long matchId, int roundNo, Long tournamentId) {
        this.matchId = matchId;
        this.roundNo = roundNo;
        this.tournamentId = tournamentId;
    }

    // Getters and setters (optional, depending on your access strategy)
    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    // equals() and hashCode() are required for composite key comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchId matchId1 = (MatchId) o;
        return roundNo == matchId1.roundNo &&
                Objects.equals(matchId, matchId1.matchId) &&
                Objects.equals(tournamentId, matchId1.tournamentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, roundNo, tournamentId);
    }
}

