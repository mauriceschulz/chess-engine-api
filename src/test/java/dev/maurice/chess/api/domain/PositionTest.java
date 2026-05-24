package dev.maurice.chess.api.domain;

import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PositionTest {

    @Test
    void fromAlgebraicTest() {
        Position position = Position.fromAlgebraic("f3");

        assertEquals(5, position.getRow());
        assertEquals(5, position.getCol());
    }

    @Test
    void toAlgebraicTest() {
        Position position = new Position(5,5);

        assertEquals(
                "f3",
                position.toAlgebraic()
        );
    }

    @Test
    void validationTestCorrectInputTest() {
        Position position = Position.fromAlgebraic("a8");

        assertTrue(position.isValid());
    }

    @Test
    void validationTestInCorrectInputTest() {
        assertThrows(
                InvalidMoveException.class,
                () -> Position.fromAlgebraic("x9")
        );
    }

    @Test
    void fromAlgebraicShouldRejectInvalidPositions() {
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic(null));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic(""));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic("a"));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic("a0"));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic("a9"));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic("i1"));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic("aa"));
        assertThrows(InvalidMoveException.class, () -> Position.fromAlgebraic("a1a"));
    }

    @Test
    void constructorShouldRejectInvalidCoordinates() {
        assertThrows(InvalidMoveException.class, () -> new Position(-1, 0));
        assertThrows(InvalidMoveException.class, () -> new Position(8, 0));
        assertThrows(InvalidMoveException.class, () -> new Position(0, -1));
        assertThrows(InvalidMoveException.class, () -> new Position(0, 8));
    }

}
