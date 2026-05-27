package dev.maurice.chess.api.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    @Test
    void createInitialShouldReturnStartingFen() {
        Board board = Board.createInitial();

        assertEquals(
                STARTING_FEN,
                board.toFen()
        );
    }

    @Test
    void fenStringShouldBeModifiedAfterMove() {
        Board board = Board.fromFen(STARTING_FEN);

        Move move = Move.fromUci("e2e4");
        board.movePiece(move);

        assertEquals(
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                board.toFen()
        );
    }

    @Test
    void fromFenShouldCreateBoardFromPiecePlacement() {
        Board board = Board.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR");

        assertEquals(
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                board.toFen()
        );
    }

    @Test
    void fromFenShouldAcceptFullFenString() {
        Board board = Board.fromFen(
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"
        );

        assertEquals(
                "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR",
                board.toFen()
        );
    }

    @Test
    void fromFenShouldRejectInvalidFenStrings() {
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen(null));
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen(""));
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen("8/8/8/8/8/8/8"));
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen("8/8/8/8/8/8/8/9"));
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen("8/8/8/8/8/8/8/X7"));
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen("8/8/8/8/8/8/8/7"));
        assertThrows(IllegalArgumentException.class, () -> Board.fromFen("8/8/8/8/8/8/8/8/"));
    }

    @Test
    void movePieceShouldMoveRookForKingSideCastling() {
        Board board = Board.fromFen("r3k2r/8/8/8/8/8/8/R3K2R");

        board.movePiece(Move.fromUci("e1g1"));

        assertEquals(
                "r3k2r/8/8/8/8/8/8/R4RK1",
                board.toFen()
        );
    }

    @Test
    void movePieceShouldMoveRookForQueenSideCastling() {
        Board board = Board.fromFen("r3k2r/8/8/8/8/8/8/R3K2R");

        board.movePiece(Move.fromUci("e1c1"));

        assertEquals(
                "r3k2r/8/8/8/8/8/8/2KR3R",
                board.toFen()
        );
    }
}
