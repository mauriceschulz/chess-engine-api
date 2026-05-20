package dev.maurice.chess.api.service;

import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<UUID, GameSession> games = new ConcurrentHashMap<>();

    public GameResponse createGame(CreateGameRequest request) {

        Color playerColor = parsePlayerColor(request.playerColor());

        GameSession game = new GameSession(UUID.randomUUID(), playerColor);

        games.put(game.getId(), game);

        return toResponse(game);
    }

    private Color parsePlayerColor(String value) {

        if (value == null || value.isBlank()) {
            return Color.WHITE;
        }
        return Color.valueOf(value.toUpperCase());
    }

    private GameResponse toResponse(GameSession game) {
        return new GameResponse(
                game.getId(),
                game.getPlayerColor().toString(),
                game.getSideToMove().toString(),
                game.getStatus().toString(),
                game.getMoveHistory()
        );
    }
}