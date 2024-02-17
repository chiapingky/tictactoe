package org.chiapingky.tictactoe.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findByGameStatus(GameStatus gameStatus);
    @Query(nativeQuery = true,
            value = "select " +
                    "g1_0.game_id, " +
                    "g1_0.board_id, " +
                    "g1_0.cross_player, " +
                    "g1_0.current_turn, " +
                    "g1_0.game_difficulty, " +
                    "g1_0.game_status, " +
                    "g1_0.round_player " +
                    "from game g1_0 " +
                    "where g1_0.game_status=?2 and (g1_0.cross_player=?1 or g1_0.round_player=?1)")
    Optional<Game> findCurrentActiveGame(String username, GameStatus gameStatus);
}
