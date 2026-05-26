package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LegalMoveGenerator {
    private final MoveValidator moveValidator;

    LegalMoveGenerator(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    public List<Move> generateLegalMoves(GameSession game, Color color) {
        List<Move> legalMoves = new ArrayList<>();
        Board board = game.getBoard();
        for (int sourceRow = 0; sourceRow < 8; sourceRow++) {
            for (int sourceCol = 0; sourceCol < 8; sourceCol++) {
                Position sourcePosition = new Position(sourceRow, sourceCol);

                Piece piece = board.getPiece(sourcePosition);

                if (piece == null || piece.color() != color) {
                    continue;
                }

                for (int targetRow = 0; targetRow < 8; targetRow++) {
                    for (int targetCol = 0; targetCol < 8; targetCol++) {
                        Position targetPosition = new Position(targetRow, targetCol);

                        try {
                            Move move = new Move(sourcePosition, targetPosition);
                            moveValidator.validate(game, move);
                            legalMoves.add(move);
                        } catch (InvalidMoveException ignored) {
                            //We do not need an error when generating invalid moves
                        }
                    }
                }
            }
        }
        return legalMoves;
    }
}