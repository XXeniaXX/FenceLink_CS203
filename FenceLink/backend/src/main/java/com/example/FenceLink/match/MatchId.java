package com.example.FenceLink.match;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class MatchId implements Serializable {
    private Long matchId;
    private int roundNo;
    private Long tournamentId;

    public MatchId() {}

    public MatchId(Long matchId, int roundNo, Long tournamentId) {
        this.matchId = matchId;
        this.roundNo = roundNo;
        this.tournamentId = tournamentId;
    }

    public Long getMatchId(){
        return matchId;
    }

    public int getRoundNo(){
        return roundNo;
    }

    public Long getTournamentId(){
        return tournamentId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

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

