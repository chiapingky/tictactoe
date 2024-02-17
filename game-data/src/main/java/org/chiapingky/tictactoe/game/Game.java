package org.chiapingky.tictactoe.game;

import jakarta.persistence.*;
import org.chiapingky.tictactoe.board.Board;
import org.springframework.data.web.config.SpringDataJacksonModules;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "game")
public class Game {
    @Id
    private String gameId;
    @Enumerated
    private GameStatus gameStatus;
    @Enumerated
    private GameDifficulty gameDifficulty;
    @OneToOne
    @JoinColumn(name = "board_id")
    private Board board;
    private String crossPlayer;
    private String roundPlayer;
    private String currentTurn;

    public Game() {
    }

    public Game(
            String gameId,
            GameStatus gameStatus,
            GameDifficulty gameDifficulty,
            Board board,
            String crossPlayer,
            String roundPlayer,
            String currentTurn
    ) {
        this.gameId = gameId;
        this.gameStatus = gameStatus;
        this.gameDifficulty = gameDifficulty;
        this.board = board;
        this.crossPlayer = crossPlayer;
        this.roundPlayer = roundPlayer;
        this.currentTurn = currentTurn;
    }

    public String getGameId() {
        return gameId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameDifficulty getGameDifficulty() {
        return gameDifficulty;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getCrossPlayer() {
        return crossPlayer;
    }

    public void setCrossPlayer(String crossPlayer) {
        this.crossPlayer = crossPlayer;
    }

    public String getRoundPlayer() {
        return roundPlayer;
    }

    public void setRoundPlayer(String roundPlayer) {
        this.roundPlayer = roundPlayer;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }
}