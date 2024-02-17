package org.chiapingky.tictactoe.gameplay;

import org.chiapingky.tictactoe.board.Board;
import org.chiapingky.tictactoe.board.Shape;
import org.chiapingky.tictactoe.game.Game;
import org.chiapingky.tictactoe.game.GameDifficulty;
import org.chiapingky.tictactoe.user.Player;
import org.springframework.stereotype.Service;

@Service
public class RuleCheckingService {
    public String checkWinner(Game game) {
        Shape result;
        if (game.getGameDifficulty().equals(GameDifficulty.NORMAL))
            result = normalCheck(game.getBoard());
        else
            result = hardCheck(game.getBoard());
        if (result == null)
            return null;
        if (result == Shape.ROUND)
            return game.getRoundPlayer();
        return game.getCrossPlayer();
    }

    private Shape normalCheck(Board board) {
        Shape[][] grid = arrangeGrid(board.getBoard(), board.getGridSize());
        //horizontal
        for (int i = 0; i < board.getGridSize(); i++) {
            for (int j = 0; j < board.getGridSize() - 2; j++) {
                if (grid[i][j].equals(Shape.BLANK))
                    continue;
                if (grid[i][j].equals(grid[i][j + 1]) && grid[i][j].equals(grid[i][j + 2]))
                    return grid[i][j];
            }
        }
        //vertical
        for (int i = 0; i < board.getGridSize(); i++) {
            for (int j = 0; j < board.getGridSize() - 2; j++) {
                if (grid[j][i].equals(Shape.BLANK))
                    continue;
                if (grid[j][i].equals(grid[j + 1][i]) && grid[j][i].equals(grid[j + 2][i]))
                    return grid[j][i];
            }
        }
        //diagonal1
        for (int i = 2; i < board.getGridSize(); i++) {
            for (int j = 0; j <= i - 2; j++) {
                int k = i - j;
                if (grid[j][k].equals(Shape.BLANK))
                    continue;
                if (grid[j][k].equals(grid[j + 1][k - 1]) &&
                        grid[j][k].equals(grid[j + 2][k - 2]))
                    return grid[j][k];
            }
        }
        for (int i = board.getGridSize() - 3; i >= 1 ; i--) {
            for (int j = board.getGridSize() - 1; j >= i + 2; j--) {
                int k = i + (board.getGridSize() - 1 - j);
                if (grid[k][j].equals(Shape.BLANK))
                    continue;
                if (grid[k][j].equals(grid[k + 1][j - 1]) &&
                        grid[k][j].equals(grid[k + 2][j - 2]))
                    return grid[k][j];
            }
        }
        //diagonal2
        for (int i = board.getGridSize() - 3; i >= 0; i--) {
            for (int j = 0; j <= board.getGridSize() - 1 - i - 2; j++) {
                int k = i + j;
                if (grid[j][k].equals(Shape.BLANK))
                    continue;
                if (grid[j][k].equals(grid[j + 1][k + 1]) &&
                        grid[j][k].equals(grid[j + 2][k + 2]))
                    return grid[j][k];
            }
        }
        for (int i = 2; i < board.getGridSize() - 1; i++) {
            for (int j = board.getGridSize() - 1; j > board.getGridSize() - 1 - i + 1; j--) {
                int k = i - (board.getGridSize() - 1 - j);
                if (grid[j][k].equals(Shape.BLANK))
                    continue;
                if (grid[j][k].equals(grid[j - 1][k - 1]) &&
                        grid[j][k].equals(grid[j - 2][k - 2]))
                    return grid[j][k];
            }
        }
        return null;
    }

    private Shape hardCheck(Board board) {
        Shape[][] grid = arrangeGrid(board.getBoard(), board.getGridSize());
        //horizontal
        for (int i = 0; i < board.getGridSize(); i++) {
            if (grid[i][0].equals(Shape.BLANK))
                continue;
            Shape curr = grid[i][0];
            boolean win = true;
            for (int j = 1; j < board.getGridSize(); j++) {
                if (!grid[i][j].equals(curr)) {
                    win = false;
                    break;
                }
            }
            if (win)
                return curr;
        }
        //vertical
        for (int i = 0; i < board.getGridSize(); i++) {
            if (grid[0][i].equals(Shape.BLANK))
                continue;
            Shape curr = grid[0][i];
            boolean win = true;
            for (int j = 1; j < board.getGridSize(); j++) {
                if (!grid[j][i].equals(curr)) {
                    win = false;
                    break;
                }
            }
            if (win)
                return curr;
        }
        //diagonal1
        Shape curr = grid[0][0];
        boolean win = true;
        if (!curr.equals(Shape.BLANK)) {
            for (int i = 0; i < board.getGridSize(); i++) {
                if (!grid[i][i].equals(curr)) {
                    win = false;
                    break;
                }
            }
        } else
            win = false;
        if (win)
            return curr;
        //diagonal2
        curr = grid[0][board.getGridSize() - 1];
        win = true;
        if (!curr.equals(Shape.BLANK)) {
            for (int i = board.getGridSize() - 2; i >= 0 ; i--) {
                if (!grid[board.getGridSize() - i - 1][i].equals(curr)) {
                    win = false;
                    break;
                }
            }
        }  else
            win = false;
        if (win)
            return curr;
        return null;
    }

    private Shape[][] arrangeGrid(Shape[] board, Integer gridSize) {
        Shape[][] result = new Shape[gridSize][gridSize];
        int k = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                result[i][j] = board[k];
                k++;
            }
        }
        return result;
    }
}
