package org.chiapingky.tictactoe.controller.model.game;

public class Room {
    private String gameId;
    private String roomOwner;
    private String difficulty;

    public Room() {
    }

    public Room(String gameId, String roomOwner, String difficulty) {
        this.gameId = gameId;
        this.roomOwner = roomOwner;
        this.difficulty = difficulty;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(String roomOwner) {
        this.roomOwner = roomOwner;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
