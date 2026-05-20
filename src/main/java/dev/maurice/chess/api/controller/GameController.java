package dev.maurice.chess.api.controller;

import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import dev.maurice.chess.api.service.GameService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@RestController
public class GameController {
    private final GameService gameService = new GameService();

    @PostMapping("api/games")
    public GameResponse createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request);
    }
}