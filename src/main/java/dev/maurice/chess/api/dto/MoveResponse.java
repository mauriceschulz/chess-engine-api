package dev.maurice.chess.api.dto;

import java.util.List;
import java.util.UUID;

public record MoveResponse(
        UUID gameId,
        String move,
        String fen,
        String sideToMove,
        String status,
        List<String> moveHistory
) {
}