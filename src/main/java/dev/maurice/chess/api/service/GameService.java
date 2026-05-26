package dev.maurice.chess.api.service;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import dev.maurice.chess.api.dto.MoveRequest;
import dev.maurice.chess.api.dto.MoveResponse;
import dev.maurice.chess.api.engine.RandomMoveEngine;
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
    private final RandomMoveEngine randomMoveEngine;

    public GameService(MoveValidator moveValidator, RandomMoveEngine randomMoveEngine) {
        this.moveValidator = moveValidator;
        this.randomMoveEngine = randomMoveEngine;
    }

    public GameResponse createGame(CreateGameRequest request) {

        Color playerColor = parsePlayerColor(request.playerColor());

        Board board = request.fen() == null || request.fen().isBlank()
                ? Board.createInitial()
                : Board.fromFen(request.fen());

        GameSession game = new GameSession(UUID.randomUUID(), playerColor, board);

        makeEngineMoveIfNeeded(game);

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

    private MoveResponse toMoveResponse(GameSession game, Move playerMove, Move engineMove) {
        return new MoveResponse(
                game.getId(),
                playerMove.toUci(),
                engineMove == null ? null : engineMove.toUci(),
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

        Move engineMove = makeEngineMoveIfNeeded(game);

        return toMoveResponse(game, move, engineMove);
    }

    private Move makeEngineMoveIfNeeded(GameSession game) {
        if (game.getSideToMove() == game.getPlayerColor()) {
            return null;
        }

        Move engineMove = randomMoveEngine.chooseMove(game);
        game.applyMove(engineMove);

        return engineMove;
    }
}
