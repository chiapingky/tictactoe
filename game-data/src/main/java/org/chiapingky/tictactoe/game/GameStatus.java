package org.chiapingky.tictactoe.game;

public enum GameStatus {
    CROSS_WIN(3),
    ROUND_WIN(2),
    DRAW(1),
    ONGOING(0);

    private final Integer value;

    GameStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
