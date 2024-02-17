package org.chiapingky.tictactoe.controller;

import org.chiapingky.tictactoe.controller.model.game.GameplayRequest;
import org.chiapingky.tictactoe.controller.model.game.GameplayResponse;
import org.chiapingky.tictactoe.controller.model.game.LeaveRequest;
import org.chiapingky.tictactoe.game.Game;
import org.chiapingky.tictactoe.gameplay.GameplayService;
import org.chiapingky.tictactoe.gameplay.exception.GameplayException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {
    @Autowired
    private GameplayService gameplayService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @CrossOrigin
    @PostMapping("/play")
    public ResponseEntity<GameplayResponse> action(@RequestBody GameplayRequest request) {
        try {
            Game game = gameplayService.makeMove(request.getGameId(), request.getMove(), request.getUsername());
            String statusMessage = gameplayService.getWinningStatusMessage(game);
            GameplayResponse response = new GameplayResponse(game, statusMessage);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + request.getGameId(), response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            GameplayResponse response;
            if (e instanceof GameplayException) {
                response = new GameplayResponse(null, e.getMessage());
            } else {
                response = new GameplayResponse(null, "Please try again");
            }
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + request.getGameId(), response);
            return ResponseEntity.ok(response);
        }
    }

    @CrossOrigin
    @PostMapping("/leave")
    public Game leave(@RequestBody LeaveRequest request) {
        try {
            return gameplayService.leaveMatch(request.getGame().getGameId(), request.getUsername());
        } catch (Exception e) {
            LoggerFactory.getLogger(GameController.class).error("Failed to leave game, ", e);
            return null;
        }
    }

}
