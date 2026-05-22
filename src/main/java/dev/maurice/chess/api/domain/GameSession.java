package dev.maurice.chess.api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession {
    private final UUID id;
    private final Board board;
    private Color playerColor;
    private Color sideToMove;
    private GameStatus status;
    List<String> moveHistory;
    private final Instant createdAt;
    private Instant updatedAt;

    public GameSession (UUID id, Color playerColor) {
        this.id = id;
        this.board = Board.createInitial();
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

    public Board getBoard() {
        return this.board;
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

    public void switchTurn () {
        this.sideToMove = sideToMove.opposite();
    }

    public void updateStatus (GameStatus status) {
        this.status = status;
    }

    public void applyMove(Move move) {
        board.movePiece(move);
        moveHistory.add(move.toUci());
        switchTurn();
        updatedAt = Instant.now();
    }
}