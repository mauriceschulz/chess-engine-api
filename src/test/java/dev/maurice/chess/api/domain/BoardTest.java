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
}