package org.chiapingky.tictactoe.controller.model.game;

import org.chiapingky.tictactoe.game.Game;

public class GameplayResponse {
    private Game game;
    private String statusMessage;

    public GameplayResponse() {
    }

    public GameplayResponse(Game game, String statusMessage) {
        this.game = game;
        this.statusMessage = statusMessage;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
