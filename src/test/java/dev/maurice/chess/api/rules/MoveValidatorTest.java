package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveValidatorTest {

    private final MoveValidator moveValidator = new MoveValidator(new PieceMovementValidator());

    @Test
    void validateShouldAcceptValidMove() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e2e4");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectMoveToSameSquare() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e2e2");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectMoveFromEmptySquare() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e3e4");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectMoveByNonActivePlayer() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e7e5");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectTargetWithOwnPiece() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e1d1");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldAcceptPawnSingleStep() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e2e3");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldAcceptBlackPawnMovesForward() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        game.switchTurn();
        Move move = Move.fromUci("e7e5");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectPawnMovingBackward() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e2e1");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectPawnDoubleStepAfterStartRank() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        game.getBoard().movePiece(Move.fromUci("e2e3"));
        Move move = Move.fromUci("e3e5");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectPawnDoubleStepWhenBlocked() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        game.getBoard().movePiece(Move.fromUci("e7e3"));
        Move move = Move.fromUci("e2e4");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldRejectPawnForwardMoveIntoOccupiedSquare() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        game.getBoard().movePiece(Move.fromUci("e7e3"));
        Move move = Move.fromUci("e2e3");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }

    @Test
    void validateShouldAcceptPawnDiagonalCapture() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        game.getBoard().movePiece(Move.fromUci("d7d3"));
        Move move = Move.fromUci("e2d3");

        assertDoesNotThrow(() -> moveValidator.validate(game, move));
    }

    @Test
    void validateShouldRejectPawnDiagonalMoveWithoutCapture() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE);
        Move move = Move.fromUci("e2d3");

        assertThrows(
                InvalidMoveException.class,
                () -> moveValidator.validate(game, move)
        );
    }
}
