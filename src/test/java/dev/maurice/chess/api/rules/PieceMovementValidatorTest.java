package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PieceMovementValidatorTest {
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    private final PieceMovementValidator validator = new PieceMovementValidator();

    @Test
    void validateShouldAcceptValidKnightMove() {
        Board board = initialBoard();
        Move move = Move.fromUci("b1c3");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectInvalidKnightMove() {
        Board board = initialBoard();
        Move move = Move.fromUci("b1b3");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldAcceptPawnSingleStep() {
        Board board = initialBoard();
        Move move = Move.fromUci("e2e3");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptPawnDoubleStepFromStartRank() {
        Board board = initialBoard();
        Move move = Move.fromUci("e2e4");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectPawnMovingBackward() {
        Board board = initialBoard();
        Move move = Move.fromUci("e2e1");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldRejectBlockedPawnDoubleStep() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/4N3/PPPPPPPP/RNBQKBNR");

        Move move = Move.fromUci("e2e4");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldRejectBlockedRookMove() {
        Board board = initialBoard();
        Move move = Move.fromUci("a1a4");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldAcceptClearRookMove() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/8/1PPPPPPP/RNBQKBNR");

        Move move = Move.fromUci("a1a4");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectBlockedBishopMove() {
        Board board = initialBoard();
        Move move = Move.fromUci("c1h6");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    @Test
    void validateShouldAcceptClearBishopMove() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPP1PPPP/RNBQKBNR");

        Move move = Move.fromUci("c1h6");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptQueenRookLikeMove() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPP1PPPP/RNBQKBNR");

        Move move = Move.fromUci("d1d4");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptQueenBishopLikeMove() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPP1PPP/RNBQKBNR");

        Move move = Move.fromUci("d1h5");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldAcceptKingOneStepMove() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPP1PPP/RNBQKBNR");

        Move move = Move.fromUci("e1e2");
        Piece piece = board.getPiece(move.getFrom());

        assertDoesNotThrow(() -> validator.validate(board, move, piece));
    }

    @Test
    void validateShouldRejectKingTwoStepMove() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPP1PPP/RNBQKBNR");

        Move move = Move.fromUci("e1e3");
        Piece piece = board.getPiece(move.getFrom());

        assertThrows(
                InvalidMoveException.class,
                () -> validator.validate(board, move, piece)
        );
    }

    private Board initialBoard() {
        return Board.fromFen(STARTING_FEN);
    }
}
