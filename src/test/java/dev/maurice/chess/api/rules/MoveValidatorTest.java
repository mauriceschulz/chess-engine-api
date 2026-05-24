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

    private final MoveValidator moveValidator = new MoveValidator();

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
}
