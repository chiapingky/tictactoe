package org.chiapingky.tictactoe.gameplay;

import org.chiapingky.tictactoe.board.Board;
import org.chiapingky.tictactoe.board.Shape;
import org.chiapingky.tictactoe.game.Game;
import org.chiapingky.tictactoe.game.GameDifficulty;
import org.chiapingky.tictactoe.game.GameStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

public class RuleCheckingServiceTest {
    public static RuleCheckingService getInstance() {
        return new RuleCheckingService();
    }

    public String getCrossString() {
        return "cross";
    }

    public String getRoundString() {
        return "round";
    }
    
    public Board generate3x3BoardCrossWin() {
        Shape[] board = {
                Shape.CROSS, Shape.BLANK, Shape.BLANK,
                Shape.BLANK, Shape.CROSS, Shape.BLANK,
                Shape.ROUND, Shape.ROUND, Shape.CROSS,
        };
        return new Board(3, board);
    }

    public Board generate6x6BoardCrossWinHardRoundWinNormal() {
        Shape[] board = {
                Shape.BLANK, Shape.CROSS, Shape.CROSS, Shape.ROUND, Shape.CROSS, Shape.CROSS,
                Shape.CROSS, Shape.ROUND, Shape.ROUND, Shape.CROSS, Shape.CROSS, Shape.BLANK,
                Shape.CROSS, Shape.ROUND, Shape.CROSS, Shape.CROSS, Shape.ROUND, Shape.ROUND,
                Shape.BLANK, Shape.BLANK, Shape.CROSS, Shape.ROUND, Shape.BLANK, Shape.BLANK,
                Shape.ROUND, Shape.CROSS, Shape.ROUND, Shape.ROUND, Shape.ROUND, Shape.ROUND,
                Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.ROUND,
        };
        return new Board(6, board);
    }

    public Board generate6x6BoardNoWinHardRoundWinNormal() {
        Shape[] board = {
                Shape.BLANK, Shape.CROSS, Shape.CROSS, Shape.ROUND, Shape.CROSS, Shape.CROSS,
                Shape.CROSS, Shape.ROUND, Shape.ROUND, Shape.CROSS, Shape.CROSS, Shape.BLANK,
                Shape.CROSS, Shape.ROUND, Shape.CROSS, Shape.CROSS, Shape.ROUND, Shape.ROUND,
                Shape.BLANK, Shape.BLANK, Shape.ROUND, Shape.ROUND, Shape.BLANK, Shape.BLANK,
                Shape.ROUND, Shape.CROSS, Shape.ROUND, Shape.ROUND, Shape.ROUND, Shape.ROUND,
                Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.ROUND,
        };
        return new Board(6, board);
    }

    public Board generate6x6BoardRoundWinNormal() {
        Shape[] board = {
                Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK,
                Shape.CROSS, Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.ROUND,
                Shape.BLANK, Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.ROUND, Shape.BLANK,
                Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.ROUND, Shape.BLANK, Shape.BLANK,
                Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.ROUND,
                Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.ROUND, Shape.ROUND,
        };
        return new Board(6, board);
    }

    public Board generate6x6BoardCrossWinNormal() {
        Shape[] board = {
                Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.ROUND, Shape.BLANK,
                Shape.CROSS, Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.ROUND,
                Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK,
                Shape.BLANK, Shape.CROSS, Shape.CROSS, Shape.ROUND, Shape.BLANK, Shape.BLANK,
                Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.BLANK, Shape.ROUND,
                Shape.BLANK, Shape.CROSS, Shape.BLANK, Shape.BLANK, Shape.ROUND, Shape.ROUND,
        };
        return new Board(6, board);
    }

    @Test
    public void checkWinner_normal_3x3_crossWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.NORMAL,
                generate3x3BoardCrossWin(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertEquals(winner, cross);
    }

    @Test
    public void checkWinner_normal_6x6_roundWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.NORMAL,
                generate6x6BoardCrossWinHardRoundWinNormal(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertEquals(winner, round);
    }

    @Test
    public void checkWinner_hard_3x3_crossWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.HARD,
                generate3x3BoardCrossWin(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertEquals(winner, cross);
    }

    @Test
    public void checkWinner_hard_6x6_crossWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.HARD,
                generate3x3BoardCrossWin(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertEquals(winner, cross);
    }

    @Test
    public void checkWinner_hard_6x6_noWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.HARD,
                generate6x6BoardNoWinHardRoundWinNormal(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertNull(winner);
    }

    @Test
    public void checkWinner_hard_6x6_RoundWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.NORMAL,
                generate6x6BoardRoundWinNormal(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertEquals(winner, round);
    }

    @Test
    public void checkWinner_hard_6x6_CrossWin() {
        String cross = getCrossString();
        String round = getRoundString();
        Game game = new Game(
                UUID.randomUUID(),
                GameStatus.ONGOING,
                GameDifficulty.NORMAL,
                generate6x6BoardCrossWinNormal(),
                cross,
                round,
                round
        );
        String winner = getInstance().checkWinner(game);
        Assert.assertEquals(winner, cross);
    }
}
