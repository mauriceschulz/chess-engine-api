package dev.maurice.chess.api.dto;

import java.util.List;
import java.util.UUID;

public record GameResponse(
        UUID gameID,
        String playerColor,
        String sideToMove,
        String status,
        List<String> moveHistory
) {
}