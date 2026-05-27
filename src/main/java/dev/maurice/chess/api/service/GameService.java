package dev.maurice.chess.api.service;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import dev.maurice.chess.api.dto.MoveRequest;
import dev.maurice.chess.api.dto.MoveResponse;
import dev.maurice.chess.api.engine.ChessEngine;
import dev.maurice.chess.api.engine.ChessEngineSelector;
import dev.maurice.chess.api.exception.GameNotFoundException;
import dev.maurice.chess.api.rules.MoveValidator;
import dev.maurice.chess.api.rules.StatusGameResolver;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<UUID, GameSession> games = new ConcurrentHashMap<>();
    private final MoveValidator moveValidator;
    private final ChessEngineSelector chessEngineSelector;
    private final StatusGameResolver statusGameResolver;

    public GameService(MoveValidator moveValidator, ChessEngineSelector chessEngineSelector, StatusGameResolver statusGameResolver) {
        this.moveValidator = moveValidator;
        this.chessEngineSelector = chessEngineSelector;
        this.statusGameResolver = statusGameResolver;
    }

    public GameResponse createGame(CreateGameRequest request) {

        Color playerColor = parsePlayerColor(request.playerColor());

        Board board = request.fen() == null || request.fen().isBlank()
                ? Board.createInitial()
                : Board.fromFen(request.fen());

        Color sideToMove = parseSideToMove(request.fen());
        EngineType engineType = parseEngineType(request.engineType());

        GameSession game = new GameSession(UUID.randomUUID(), playerColor, board, sideToMove, engineType);
        game.updateStatus(statusGameResolver.resolve(game));
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

    private EngineType parseEngineType(String value) {
        if (value == null || value.isBlank()) {
            return EngineType.RANDOM;
        }

        return EngineType.valueOf(value.toUpperCase());
    }

    private Color parseSideToMove(String fen) {
        if (fen == null || fen.isBlank()) {
            return Color.WHITE;
        }

        String[] fields = fen.trim().split("\\s+");
        if (fields.length < 2) {
            return Color.WHITE;
        }

        return switch (fields[1]) {
            case "w" -> Color.WHITE;
            case "b" -> Color.BLACK;
            default -> throw new IllegalArgumentException("Invalid active color in FEN: " + fields[1]);
        };
    }

    private GameResponse toGameResponse(GameSession game) {
        return new GameResponse(
                game.getId(),
                game.getBoard().toFen(),
                game.getPlayerColor().name(),
                game.getEngineType().name(),
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
                game.getEngineType().name(),
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

        game.updateStatus(statusGameResolver.resolve(game));

        Move engineMove = makeEngineMoveIfNeeded(game);

        return toMoveResponse(game, move, engineMove);
    }

    private Move makeEngineMoveIfNeeded(GameSession game) {
        if (game.getStatus() != GameStatus.ACTIVE) {
            return null;
        }

        if (game.getSideToMove() == game.getPlayerColor()) {
            return null;
        }

        ChessEngine engine = chessEngineSelector.select(game.getEngineType());
        Move engineMove = engine.chooseMove(game);
        game.applyMove(engineMove);

        game.updateStatus(statusGameResolver.resolve(game));

        return engineMove;
    }
}
