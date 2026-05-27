package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpeningBookTest {

    private final OpeningBook openingBook = new OpeningBook();

    @Test
    void findMoveShouldReturnLegalBookMoveForStartingPosition() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE, Board.createInitial());

        Optional<Move> move = openingBook.findMove(
                game,
                List.of(
                        Move.fromUci("e2e4"),
                        Move.fromUci("d2d4"),
                        Move.fromUci("a2a3")
                )
        );

        assertTrue(move.isPresent());
        assertTrue(Set.of("e2e4", "d2d4").contains(move.get().toUci()));
    }

    @Test
    void findMoveShouldReturnLegalBookResponseForKnownLine() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE, Board.createInitial());
        game.applyMove(Move.fromUci("e2e4"));

        Optional<Move> move = openingBook.findMove(
                game,
                List.of(
                        Move.fromUci("e7e5"),
                        Move.fromUci("a7a6")
                )
        );

        assertTrue(move.isPresent());
        assertEquals("e7e5", move.get().toUci());
    }

    @Test
    void findMoveShouldReturnEmptyWhenNoBookMoveIsLegal() {
        GameSession game = new GameSession(UUID.randomUUID(), Color.WHITE, Board.createInitial());

        Optional<Move> move = openingBook.findMove(
                game,
                List.of(Move.fromUci("a2a3"))
        );

        assertTrue(move.isEmpty());
    }

    @Test
    void findMoveShouldReturnEmptyForCustomPositionWithoutHistory() {
        GameSession game = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR")
        );

        Optional<Move> move = openingBook.findMove(
                game,
                List.of(Move.fromUci("d2d4"))
        );

        assertTrue(move.isEmpty());
    }
}
