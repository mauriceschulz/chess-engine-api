package dev.maurice.chess.api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession {
    private final UUID id;
    private Color playerColor;
    private Color sideToMove;
    private GameStatus status;
    List<String> moveHistory;
    private final Instant createdAt;
    private Instant updatedAt;

    public GameSession (Color playerColor) {
        this.id = UUID.randomUUID();
        this.playerColor = playerColor;
        this.sideToMove = Color.WHITE;
        this.status = GameStatus.ACTIVE;
        this.moveHistory = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return this.id;
    }

    public Color getPlayerColor() {
        return this.playerColor;
    }

    public Color getSideToMove() {
        return this.sideToMove;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public List<String> getMoveHistory() {
        return this.moveHistory;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void addMove () {

    }

    public void switchTurn () {
        this.sideToMove = sideToMove.opposite();
    }

    public void updateStatus (GameStatus status) {
        this.status = status;
    }
}