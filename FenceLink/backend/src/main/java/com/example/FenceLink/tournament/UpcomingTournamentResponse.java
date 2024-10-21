package com.example.FenceLink.tournament;

public class UpcomingTournamentResponse {
    private Tournament tournament;
    private String clashMessage;

    public UpcomingTournamentResponse(Tournament tournament, String clashMessage) {
        this.tournament = tournament;
        this.clashMessage = clashMessage;
    }

    // Getters and setters
    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getClashMessage() {
        return clashMessage;
    }

    public void setClashMessage(String clashMessage) {
        this.clashMessage = clashMessage;
    }
}
