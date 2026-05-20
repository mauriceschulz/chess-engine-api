package dev.maurice.chess.api.service;

import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;

public class GameService {

    public GameResponse createGame(CreateGameRequest request) {
        Color playerColor = parsePlayerColor(request.playerColor());
        GameSession gameSession = new GameSession(playerColor);
        return toResponse(gameSession);
    }

    private Color parsePlayerColor(String playerColor) {
        Color color = null;
        switch (playerColor.toLowerCase()) {
            case "white" -> {color = Color.WHITE;}
            case "black" -> {color = Color.BLACK;}
        };
        return color;
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