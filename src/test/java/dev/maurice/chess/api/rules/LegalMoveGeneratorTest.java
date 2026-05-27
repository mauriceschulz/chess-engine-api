package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegalMoveGeneratorTest {

    private final PieceMovementValidator pieceMovementValidator =
            new PieceMovementValidator();

    private final CheckValidator checkValidator =
            new CheckValidator(pieceMovementValidator);

    private final CastlingValidator castlingValidator =
            new CastlingValidator(checkValidator);

    private final MoveValidator moveValidator =
            new MoveValidator(
                    pieceMovementValidator,
                    checkValidator,
                    castlingValidator
            );

    private final LegalMoveGenerator legalMoveGenerator =
            new LegalMoveGenerator(moveValidator);

    @Test
    void generateLegalMovesShouldReturn20MovesInStartingPosition() {

        GameSession game = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.createInitial()
        );

        List<?> legalMoves =
                legalMoveGenerator.generateLegalMoves(
                        game,
                        Color.WHITE
                );

        assertEquals(20, legalMoves.size());
    }

    @Test
    void generateCommonStartingMoveE2E4() {
        GameSession gameSession = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.createInitial()
        );

        List<Move> legalMoves =
                legalMoveGenerator.generateLegalMoves(
                        gameSession,
                        Color.WHITE
                );

        assertTrue(
                legalMoves.stream().anyMatch(move -> move.toUci().equals("e2e4"))
        );
    }

    @Test
    void generateLegalMovesShouldIncludeCastlingWhenLegal() {
        GameSession gameSession = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.fromFen("r3k2r/8/8/8/8/8/8/R3K2R")
        );

        List<Move> legalMoves =
                legalMoveGenerator.generateLegalMoves(
                        gameSession,
                        Color.WHITE
                );

        assertTrue(
                legalMoves.stream().anyMatch(move -> move.toUci().equals("e1g1"))
        );
        assertTrue(
                legalMoves.stream().anyMatch(move -> move.toUci().equals("e1c1"))
        );
    }

    @Test
    void generateLegalMovesShouldAllowBlackKingSideCastling() {
        GameSession gameSession = new GameSession(
                UUID.randomUUID(),
                Color.BLACK,
                Board.fromFen("r3k2r/8/8/8/8/8/8/R3K2R"),
                Color.BLACK
        );

        List<Move> legalMoves =
                legalMoveGenerator.generateLegalMoves(
                        gameSession,
                        Color.BLACK
                );

        assertTrue(
                legalMoves.stream().anyMatch(move -> move.toUci().equals("e8g8"))
        );
    }

    @Test
    void generateLegalMovesShouldAllowBlackQueenSideCastling() {
        GameSession gameSession = new GameSession(
                UUID.randomUUID(),
                Color.BLACK,
                Board.fromFen("r3k2r/8/8/8/8/8/8/R3K2R"),
                Color.BLACK
        );

        List<Move> legalMoves =
                legalMoveGenerator.generateLegalMoves(
                        gameSession,
                        Color.BLACK
                );

        assertTrue(
                legalMoves.stream().anyMatch(move -> move.toUci().equals("e8c8"))
        );
    }

    @Test
    void generateLegalMovesShouldIncludeEnPassantWhenLegal() {
        GameSession gameSession = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.fromFen("4k3/8/8/3pP3/8/8/8/4K3")
        );
        gameSession.getMoveHistory().add("d7d5");

        List<Move> legalMoves =
                legalMoveGenerator.generateLegalMoves(
                        gameSession,
                        Color.WHITE
                );

        assertTrue(
                legalMoves.stream().anyMatch(move -> move.toUci().equals("e5d6"))
        );
    }
}
