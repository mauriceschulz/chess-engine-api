package dev.maurice.chess.api.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameSession {
    private final UUID id;
    private final Board board;
    private final Color playerColor;
    private final EngineType engineType;
    private final CastlingRights castlingRights;
    private Color sideToMove;
    private GameStatus status;
    List<String> moveHistory;
    private final Instant createdAt;
    private Instant updatedAt;

    public GameSession(UUID id, Color playerColor, Board board, Color sideToMove, EngineType engineType) {
        this.id = id;
        this.board = board;
        this.playerColor = playerColor;
        this.engineType = engineType;
        this.sideToMove = sideToMove;
        this.status = GameStatus.ACTIVE;
        this.castlingRights = CastlingRights.initial();
        this.moveHistory = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public GameSession(UUID id, Color playerColor, Board board, Color sideToMove) {
        this(id, playerColor, board, sideToMove, EngineType.RANDOM);
    }

    public GameSession(UUID id, Color playerColor, Board board) {
        this(id, playerColor, board, Color.WHITE);
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

    public EngineType getEngineType() {
        return this.engineType;
    }

    public Color getSideToMove() {
        return this.sideToMove;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public CastlingRights getCastlingRights() {
        return this.castlingRights;
    }

    public void updateCastleRights(Move move, Piece movedPiece) {
        updateCastleRights(move, movedPiece, null);
    }

    public void updateCastleRights(Move move, Piece movedPiece, Piece capturedPiece) {
        if (movedPiece == null) {
            return;
        }

        if (movedPiece.type() == PieceType.KING) {
            castlingRights.removeCastlingRights(movedPiece.color());
        } else if (movedPiece.type() == PieceType.ROOK) {
            removeCastlingRightForRookSquare(move.getFrom());
        }

        if (capturedPiece != null && capturedPiece.type() == PieceType.ROOK) {
            removeCastlingRightForRookSquare(move.getTo());
        }
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
        this.updatedAt = Instant.now();
    }

    public void applyMove(Move move) {
        Piece movedPiece = board.getPiece(move.getFrom());
        Piece capturedPiece = board.getPiece(move.getTo());

        board.movePiece(move);
        updateCastleRights(move, movedPiece, capturedPiece);

        moveHistory.add(move.toUci());
        switchTurn();
        updatedAt = Instant.now();
    }

    private void removeCastlingRightForRookSquare(Position position) {
        switch (position.toAlgebraic()) {
            case "a1" -> castlingRights.removeQueenSide(Color.WHITE);
            case "h1" -> castlingRights.removeKingSide(Color.WHITE);
            case "a8" -> castlingRights.removeQueenSide(Color.BLACK);
            case "h8" -> castlingRights.removeKingSide(Color.BLACK);
        }
    }
}
