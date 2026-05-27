package dev.maurice.chess.api.dto;

public record CreateGameRequest (
    String playerColor,
    String fen,
    String engineType
) {

    public CreateGameRequest(String playerColor, String fen) {
        this(playerColor, fen, null);
    }
}
