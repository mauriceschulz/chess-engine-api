package dev.maurice.chess.api.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {

    @Test
    void fromAlgebraicTest() {
        Position position = Position.fromAlgebraic("f3");

        // TODO: finish this test
    }

    @Test
    void toAlgebraicTest() {
        Position position = new Position(5,5);

        assertEquals(
                "f3",
                position.toAlgebraic()
        );
    }


}
