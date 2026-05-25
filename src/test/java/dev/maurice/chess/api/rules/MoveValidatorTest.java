package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveValidatorTest {
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    private final PieceMovementValidator pieceMovementValidator = new PieceMovementValidator();
    private final MoveValidator moveValidator = new MoveValidator(pieceMovementValidator,
                                                                    new CheckValidator(pieceMovementValidator));

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

    private GameSession newGame(String fen) {
        return new GameSession(UUID.randomUUID(), Color.WHITE, Board.fromFen(fen));
    }
}
