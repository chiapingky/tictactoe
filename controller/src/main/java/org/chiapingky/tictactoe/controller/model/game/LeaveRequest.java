package org.chiapingky.tictactoe.controller.model.game;

import org.chiapingky.tictactoe.game.Game;

public class LeaveRequest {
    private Game game;
    private String username;

    public LeaveRequest() {
    }

    public LeaveRequest(Game game, String username) {
        this.game = game;
        this.username = username;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
