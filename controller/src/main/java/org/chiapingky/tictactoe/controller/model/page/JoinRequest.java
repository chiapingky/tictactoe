package org.chiapingky.tictactoe.controller.model.page;

public class JoinRequest {
    private String gameId;

    public JoinRequest() {
        gameId = "";
    }

    public JoinRequest(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
