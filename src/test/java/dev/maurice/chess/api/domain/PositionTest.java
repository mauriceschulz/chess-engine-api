package dev.maurice.chess.api.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


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
        Position position = Position.fromAlgebraic("x9");

        assertFalse(position.isValid());
    }

}
