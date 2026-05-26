package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
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

    private final MoveValidator moveValidator =
            new MoveValidator(
                    pieceMovementValidator,
                    checkValidator
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
}