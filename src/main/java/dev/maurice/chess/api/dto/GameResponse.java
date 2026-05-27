package dev.maurice.chess.api.dto;

import java.util.List;
import java.util.UUID;

public record GameResponse(
        UUID gameId,
        String fen,
        String playerColor,
        String engineType,
        String sideToMove,
        String status,
        List<String> moveHistory
) {
}
