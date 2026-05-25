package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PieceMovementValidatorTest {

    private final PieceMovementValidator validator = new PieceMovementValidator();

    @Test
    void validateShouldAcceptValidKnightMove() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("b1c3");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectInvalidKnightMove() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("b1b3");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldAcceptPawnSingleStep() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("e2e3");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptPawnDoubleStepFromStartRank() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("e2e4");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectPawnMovingBackward() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("e2e1");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldRejectBlockedPawnDoubleStep() {
        Board board = Board.createInitial();
        board.setPiece(
                Position.fromAlgebraic("e3"),
                new Piece(PieceType.KNIGHT, Color.WHITE)
        );

        Move move = Move.fromUci("e2e4");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldRejectBlockedRookMove() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("a1a4");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldAcceptClearRookMove() {
        Board board = Board.createInitial();

        board.setPiece(Position.fromAlgebraic("a2"), null);
        board.setPiece(Position.fromAlgebraic("a3"), null);

        Move move = Move.fromUci("a1a4");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectBlockedBishopMove() {
        Board board = Board.createInitial();
        Move move = Move.fromUci("c1h6");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldAcceptClearBishopMove() {
        Board board = Board.createInitial();

        board.setPiece(Position.fromAlgebraic("d2"), null);
        board.setPiece(Position.fromAlgebraic("e3"), null);
        board.setPiece(Position.fromAlgebraic("f4"), null);
        board.setPiece(Position.fromAlgebraic("g5"), null);

        Move move = Move.fromUci("c1h6");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptQueenRookLikeMove() {
        Board board = Board.createInitial();

        board.setPiece(Position.fromAlgebraic("d2"), null);
        board.setPiece(Position.fromAlgebraic("d3"), null);

        Move move = Move.fromUci("d1d4");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptQueenBishopLikeMove() {
        Board board = Board.createInitial();

        board.setPiece(Position.fromAlgebraic("e2"), null);
        board.setPiece(Position.fromAlgebraic("f3"), null);
        board.setPiece(Position.fromAlgebraic("g4"), null);

        Move move = Move.fromUci("d1h5");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptKingOneStepMove() {
        Board board = Board.createInitial();

        board.setPiece(Position.fromAlgebraic("e2"), null);

        Move move = Move.fromUci("e1e2");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectKingTwoStepMove() {
        Board board = Board.createInitial();

        board.setPiece(Position.fromAlgebraic("e2"), null);
        board.setPiece(Position.fromAlgebraic("e3"), null);

        Move move = Move.fromUci("e1e3");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }
}