package dev.maurice.chess.api.controller;

import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import dev.maurice.chess.api.dto.MoveRequest;
import dev.maurice.chess.api.dto.MoveResponse;
import dev.maurice.chess.api.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public GameResponse createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request);
    }

    @PostMapping("/{gameId}/moves")
    public MoveResponse makeMove(
            @PathVariable UUID gameId,
            @RequestBody MoveRequest request
    ) {
        return gameService.makeMove(gameId, request);
    }
}