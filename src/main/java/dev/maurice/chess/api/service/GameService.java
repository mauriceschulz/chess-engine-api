package dev.maurice.chess.api.service;

import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import dev.maurice.chess.api.dto.MoveRequest;
import dev.maurice.chess.api.dto.MoveResponse;
import dev.maurice.chess.api.exception.GameNotFoundException;
import dev.maurice.chess.api.rules.MoveValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<UUID, GameSession> games = new ConcurrentHashMap<>();
    private final MoveValidator moveValidator;

    public GameService(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    public GameResponse createGame(CreateGameRequest request) {

        Color playerColor = parsePlayerColor(request.playerColor());

        GameSession game = new GameSession(UUID.randomUUID(), playerColor);

        games.put(game.getId(), game);

        return toGameResponse(game);
    }

    private Color parsePlayerColor(String value) {

        if (value == null || value.isBlank()) {
            return Color.WHITE;
        }
        return Color.valueOf(value.toUpperCase());
    }

    private GameResponse toGameResponse(GameSession game) {
        return new GameResponse(
                game.getId(),
                game.getBoard().toFen(),
                game.getPlayerColor().name(),
                game.getSideToMove().name(),
                game.getStatus().name(),
                List.copyOf(game.getMoveHistory())
        );
    }

    private MoveResponse toMoveResponse(GameSession game, Move move) {
        return new MoveResponse(
                game.getId(),
                move.toUci(),
                game.getBoard().toFen(),
                game.getSideToMove().name(),
                game.getStatus().name(),
                List.copyOf(game.getMoveHistory())
        );
    }

    public MoveResponse makeMove(UUID gameId, MoveRequest request) {
        GameSession game = games.get(gameId);

        if (game == null) {
            throw new GameNotFoundException(gameId);
        }

        Move move = Move.fromUci(request.move());
        moveValidator.validate(game, move);
        game.applyMove(move);
        return toMoveResponse(game, move);
    }
}