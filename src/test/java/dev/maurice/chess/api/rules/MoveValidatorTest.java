package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveValidatorTest {
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    private final PieceMovementValidator pieceMovementValidator = new PieceMovementValidator();
    private final CheckValidator checkValidator = new CheckValidator(pieceMovementValidator);
    private final MoveValidator moveValidator = new MoveValidator(pieceMovementValidator,
                                                                    checkValidator,
                                                                    new CastlingValidator(checkValidator));

    @Test
    void validateShouldAcceptValidMove() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e2e4");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectMoveToSameSquare() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e2e2");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectMoveFromEmptySquare() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e3e4");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectMoveByNonActivePlayer() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e7e5");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectTargetWithOwnPiece() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e1d1");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldAcceptPawnSingleStep() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e2e3");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldAcceptBlackPawnMovesForward() {
        GameSession game = newGame(STARTING_FEN);
        game.switchTurn();
        Move move = Move.fromUci("e7e5");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectPawnMovingBackward() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e2e1");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectPawnDoubleStepAfterStartRank() {
        GameSession game = newGame("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR");
        Move move = Move.fromUci("e3e5");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectPawnDoubleStepWhenBlocked() {
        GameSession game = newGame("rnbqkbnr/pppp1ppp/8/8/8/4p3/PPPPPPPP/RNBQKBNR");
        Move move = Move.fromUci("e2e4");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectPawnForwardMoveIntoOccupiedSquare() {
        GameSession game = newGame("rnbqkbnr/pppp1ppp/8/8/8/4p3/PPPPPPPP/RNBQKBNR");
        Move move = Move.fromUci("e2e3");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldAcceptPawnDiagonalCapture() {
        GameSession game = newGame("rnbqkbnr/ppp1pppp/8/8/8/3p4/PPPPPPPP/RNBQKBNR");
        Move move = Move.fromUci("e2d3");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectPawnDiagonalMoveWithoutCapture() {
        GameSession game = newGame(STARTING_FEN);
        Move move = Move.fromUci("e2d3");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldAcceptKingSideCastlingWhenLegal() {
        GameSession game = newGame("r3k2r/8/8/8/8/8/8/R3K2R");
        Move move = Move.fromUci("e1g1");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldAcceptQueenSideCastlingWhenLegal() {
        GameSession game = newGame("r3k2r/8/8/8/8/8/8/R3K2R");
        Move move = Move.fromUci("e1c1");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectCastlingThroughCheck() {
        GameSession game = newGame("r3kr1r/8/8/8/8/8/8/R3K2R");
        Move move = Move.fromUci("e1g1");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectCastlingAfterRookMoved() {
        GameSession game = newGame("r3k2r/8/8/8/8/8/8/R3K2R");

        game.applyMove(Move.fromUci("h1h2"));
        game.switchTurn();
        game.applyMove(Move.fromUci("h2h1"));
        game.switchTurn();

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, Move.fromUci("e1g1"))
        );
    }

    private GameSession newGame(String fen) {
        return new GameSession(UUID.randomUUID(), Color.WHITE, Board.fromFen(fen));
    }
}
