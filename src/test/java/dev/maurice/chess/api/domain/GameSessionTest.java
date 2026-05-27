package dev.maurice.chess.api.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {

    @Test
    void copyShouldCreateIndependentSessionWithSameAttributes() {
        UUID id = UUID.randomUUID();
        GameSession original = new GameSession(
                id,
                Color.BLACK,
                Board.createInitial(),
                Color.WHITE,
                EngineType.ONE_PLY
        );

        original.applyMove(Move.fromUci("e2e4"));
        original.updateStatus(GameStatus.DRAW);

        GameSession copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getBoard(), copy.getBoard());
        assertNotSame(original.getCastlingRights(), copy.getCastlingRights());
        assertNotSame(original.getMoveHistory(), copy.getMoveHistory());

        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getPlayerColor(), copy.getPlayerColor());
        assertEquals(original.getEngineType(), copy.getEngineType());
        assertEquals(original.getSideToMove(), copy.getSideToMove());
        assertEquals(original.getStatus(), copy.getStatus());
        assertEquals(original.getCreatedAt(), copy.getCreatedAt());
        assertEquals(original.getUpdatedAt(), copy.getUpdatedAt());
        assertEquals(original.getBoard().toFen(), copy.getBoard().toFen());
        assertEquals(original.getMoveHistory(), copy.getMoveHistory());
    }

    @Test
    void movesOnCopyShouldNotChangeOriginalSession() {
        GameSession original = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.createInitial()
        );

        original.applyMove(Move.fromUci("e2e4"));
        GameSession copy = original.copy();

        copy.applyMove(Move.fromUci("e7e5"));

        assertEquals(
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                original.getBoard().toFen()
        );
        assertEquals(
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR",
                copy.getBoard().toFen()
        );

        assertEquals(1, original.getMoveHistory().size());
        assertEquals(2, copy.getMoveHistory().size());
        assertEquals(Color.BLACK, original.getSideToMove());
        assertEquals(Color.WHITE, copy.getSideToMove());
    }

    @Test
    void castlingRightsOnCopyShouldNotChangeOriginalSession() {
        GameSession original = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.fromFen("r3k2r/8/8/8/8/8/8/R3K2R")
        );

        original.applyMove(Move.fromUci("h1h2"));
        GameSession copy = original.copy();

        copy.applyMove(Move.fromUci("a1a2"));

        assertFalse(original.getCastlingRights().canCastle(Color.WHITE, true));
        assertTrue(original.getCastlingRights().canCastle(Color.WHITE, false));
        assertFalse(copy.getCastlingRights().canCastle(Color.WHITE, true));
        assertFalse(copy.getCastlingRights().canCastle(Color.WHITE, false));
    }
}
