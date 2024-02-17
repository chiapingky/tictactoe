package org.chiapingky.tictactoe.controller.model.game;

public class GameplayRequest {
    private String gameId;
    private String username;
    private Integer move;

    public GameplayRequest() {
    }

    public GameplayRequest(String gameId, String username, Integer move) {
        this.gameId = gameId;
        this.username = username;
        this.move = move;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMove() {
        return move;
    }

    public void setMove(Integer move) {
        this.move = move;
    }
}
