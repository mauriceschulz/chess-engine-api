package dev.maurice.chess.api.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveTest {

    @Test
    void createMoveFromUciTest() {
        Move move = Move.fromUci("e6h8");

        Position expectedFrom = Position.fromAlgebraic("e6");
        Position expectedTo = Position.fromAlgebraic("h8");

        assertEquals(expectedFrom.toAlgebraic(), move.getFrom().toAlgebraic());
        assertEquals(expectedTo.toAlgebraic(), move.getTo().toAlgebraic());
    }

    @Test
    void canConvertMoveToCorrectUci() {
        assertEquals(
                "a1h8",
                Move.fromUci("a1h8").toUci()
        );
    }

    @Test
    void canCorrectlyParsePromotions() {
        assertEquals(
                "e7e8q",
                Move.fromUci("e7e8q").toUci()
        );
    }

    @Test
    void canCorrectlyParseKnightPromotion() {
        assertEquals(
                "e7e8n",
                Move.fromUci("e7e8n").toUci()
        );
    }
}
