package dev.maurice.chess.api.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {

    @Test
    void createInitialShouldReturnStartingFen() {
        Board board = Board.createInitial();

        assertEquals(
                "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
                board.toFen()
        );
    }

    @Test
    void fenStringShouldBeModifiedAfterMove() {
        Board board = Board.createInitial();

        Move move = Move.fromUci("e2e4");
        board.movePiece(move);

        assertEquals(
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                board.toFen()
        );
    }
}