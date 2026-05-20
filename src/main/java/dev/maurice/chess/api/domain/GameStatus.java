package dev.maurice.chess.api.domain;

public enum GameStatus {
    ACTIVE,
    CHECK_MATE,
    DRAW,
    STALE_MATE,
    RESIGNED
}