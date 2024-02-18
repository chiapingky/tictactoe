package org.chiapingky.tictactoe.gameplay;

import org.chiapingky.tictactoe.board.Board;
import org.chiapingky.tictactoe.board.BoardRepository;
import org.chiapingky.tictactoe.board.Shape;
import org.chiapingky.tictactoe.game.Game;
import org.chiapingky.tictactoe.game.GameDifficulty;
import org.chiapingky.tictactoe.game.GameRepository;
import org.chiapingky.tictactoe.game.GameStatus;
import org.chiapingky.tictactoe.gameplay.exception.GameplayException;
import org.chiapingky.tictactoe.user.Player;
import org.chiapingky.tictactoe.user.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@Service
public class GameplayService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameplayService.class);
    @Autowired
    GameRepository gameRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    RuleCheckingService ruleCheckingService;

    @Transactional
    public Game newGame(String creator, Integer gridSize, String difficulty) throws Exception {
        try {
            Player player = getPlayer(creator);
            GameDifficulty gameDifficulty = GameDifficulty.valueOf(difficulty);
            if (gridSize < 3 || gridSize > 10)
                throw new GameplayException("Invalid grid size");
            Shape[] board = new Shape[gridSize*gridSize];
            Arrays.fill(board, Shape.BLANK);
            String crossPlayer = null, roundPlayer = null;
            if (new Random().nextBoolean())
                crossPlayer = player.getUsername();
            else
                roundPlayer = player.getUsername();
            Board gameboard = new Board(UUID.randomUUID().toString(), gridSize, board);
            Game newGame = new Game(
                    UUID.randomUUID().toString(),
                    GameStatus.ONGOING,
                    gameDifficulty,
                    gameboard,
                    crossPlayer,
                    roundPlayer,
                    null
            );
            boardRepository.save(gameboard);
            return gameRepository.save(newGame);
        } catch (Exception e) {
            LOGGER.info("Failed to start game: ", e);
            throw e;
        }
    }

    public Game joinGame(String gameId, String joiner) throws Exception {
        try {
            Player player = getPlayer(joiner);
            Game game = getGameById(gameId);
            if (game.getCrossPlayer() != null) {
                if (!game.getCrossPlayer().equals(joiner))
                    game.setRoundPlayer(player.getUsername());
                else
                    throw new GameplayException("Can't join your own room");
            } else {
                if (!game.getRoundPlayer().equals(joiner))
                    game.setCrossPlayer(player.getUsername());
                else
                    throw new GameplayException("Can't join your own room");
            }
            if (new Random().nextBoolean())
                game.setCurrentTurn(game.getCrossPlayer());
            else
                game.setCurrentTurn(game.getRoundPlayer());
            return gameRepository.save(game);
        } catch (Exception e) {
            LOGGER.info("Failed to join game: ", e);
            throw e;
        }
    }

    @Transactional
    public Game makeMove(String gameId, Integer move, String player) throws Exception {
        try {
            Game game = getGameById(gameId);
            if (move == -1)
                return game;
            if (move == -2 && game.getGameStatus().equals(GameStatus.ONGOING)) {
                String opponent = player.equals(game.getRoundPlayer()) ? game.getCrossPlayer() : game.getRoundPlayer();
                throw new GameplayException(player + " left the match. " + opponent + " win!");
            }
            if (game.getCrossPlayer() == null || game.getRoundPlayer() == null)
                throw new GameplayException("Wait for opponent to join");
            if (!game.getGameStatus().equals(GameStatus.ONGOING)) {
                if (game.getGameStatus().equals(GameStatus.ROUND_WIN))
                    throw new GameplayException("Game already ended. " + game.getRoundPlayer() + " win the game!");
                else if (game.getGameStatus().equals(GameStatus.CROSS_WIN))
                    throw new GameplayException("Game already ended. " + game.getCrossPlayer() + " win the game!");
                else if (game.getGameStatus().equals(GameStatus.DRAW))
                    throw new GameplayException("Game already ended. It's a tie!");
            }
            if (!game.getCurrentTurn().equals(player) && player.equals(game.getCrossPlayer()))
                throw new GameplayException("Still " + game.getRoundPlayer() + "'s turn");
            if (!game.getCurrentTurn().equals(player) && player.equals(game.getRoundPlayer()))
                throw new GameplayException("Still " + game.getCrossPlayer() + "'s turn");
            if (!game.getBoard().getBoard()[move].equals(Shape.BLANK))
                throw new GameplayException("Invalid move");
            Board newBoard = game.getBoard();
            if (game.getCrossPlayer().equals(player)) {
                newBoard.getBoard()[move] = Shape.CROSS;
                game.setCurrentTurn(game.getRoundPlayer());
            } else {
                newBoard.getBoard()[move] = Shape.ROUND;
                game.setCurrentTurn(game.getCrossPlayer());
            }
            game.setBoard(newBoard);
            String winner = ruleCheckingService.checkWinner(game);
            if (winner == null) {
                if (!hasEmptyCell(game.getBoard().getBoard()))
                    game.setGameStatus(GameStatus.DRAW);
                else
                    game.setGameStatus(GameStatus.ONGOING);
            } else {
                if (winner.equals(game.getCrossPlayer()))
                    game.setGameStatus(GameStatus.CROSS_WIN);
                else if (winner.equals(game.getRoundPlayer()))
                    game.setGameStatus(GameStatus.ROUND_WIN);
            }
            return gameRepository.save(game);
        } catch (Exception e) {
            LOGGER.info("Failed to make move: ", e);
            throw e;
        }
    }

    public String getWinningStatusMessage(Game game) {
        String crossPlayer = game.getCrossPlayer();
        String roundPlayer = game.getRoundPlayer();
        if (crossPlayer == null || crossPlayer.isEmpty() || roundPlayer == null || roundPlayer.isEmpty()) {
            return "Waiting for opponent to join . . .";
        } else {
            return switch (game.getGameStatus()) {
                case ONGOING -> game.getCurrentTurn() + " turns";
                case CROSS_WIN -> crossPlayer + " win the game!";
                case ROUND_WIN -> roundPlayer + " win the game!";
                default -> "It's a tie!";
            };
        }
    }

    public Game leaveMatch(String gameId, String username) throws Exception {
        try {
            Game game = getGameById(gameId);
            if (!game.getGameStatus().equals(GameStatus.ONGOING))
                return game;
            else if (username.equals(game.getRoundPlayer()))
                game.setGameStatus(GameStatus.CROSS_WIN);
            else if (username.equals(game.getCrossPlayer()))
                game.setGameStatus(GameStatus.ROUND_WIN);
            else
                throw new Exception("Invalid action");
            return gameRepository.save(game);
        } catch (Exception e) {
            LOGGER.info("Failed to leave match: ", e);
            throw e;
        }
    }

    private Player getPlayer(String player) throws Exception {
        Player existing = playerRepository.findById(player).orElse(null);
        if (existing == null) {
            throw new GameplayException("Player not found");
        }
        return existing;
    }

    private Game getGameById(String gameId) throws Exception {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null)
            throw new GameplayException("Game ID not found in database");
        return game;
    }

    private boolean hasEmptyCell(Shape[] board) {
        for (Shape cell : board) {
            if (cell.equals(Shape.BLANK))
                return true;
        }
        return false;
    }
}
