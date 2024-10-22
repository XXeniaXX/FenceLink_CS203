package com.example.FenceLink.match;

import java.io.Serializable;

public class MatchId implements Serializable {
    private Long matchId;
    private int roundNo;
    private Long tournamentId;

    // Default constructor, equals, and hashCode
    public MatchId(Long matchId2, int roundNo2, Long tournamentId2) {}

    // Getters and setters, equals and hashCode implementations
    public Long getMatchId(){
        return matchId;
    }

    public int getRoundNo(){
        return roundNo;
    }

    public Long getTournamentId(){
        return tournamentId;
    }
}

