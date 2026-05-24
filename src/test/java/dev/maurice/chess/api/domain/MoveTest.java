package dev.maurice.chess.api.domain;

import dev.maurice.chess.api.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void fromUciShouldRejectInvalidBoardCoordinates() {
        assertThrows(
                InvalidMoveException.class,
                () -> Move.fromUci("x9z8")
        );
    }

    @Test
    void fromUciShouldRejectInvalidMoveFormats() {
        assertThrows(InvalidMoveException.class, () -> Move.fromUci(null));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci(""));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("e2"));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("e2e"));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("e2e4qq"));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("i2e4"));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("e0e4"));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("e2e9"));
        assertThrows(InvalidMoveException.class, () -> Move.fromUci("e2e4k"));
    }

    @Test
    void constructorShouldRejectMissingPositions() {
        Position e2 = Position.fromAlgebraic("e2");
        Position e4 = Position.fromAlgebraic("e4");

        assertThrows(InvalidMoveException.class, () -> new Move(null, e4));
        assertThrows(InvalidMoveException.class, () -> new Move(e2, null));
    }

    @Test
    void constructorShouldRejectInvalidPromotionPieces() {
        Position e7 = Position.fromAlgebraic("e7");
        Position e8 = Position.fromAlgebraic("e8");

        assertThrows(InvalidMoveException.class, () -> new Move(e7, e8, PieceType.KING));
        assertThrows(InvalidMoveException.class, () -> new Move(e7, e8, PieceType.PAWN));
    }
}
