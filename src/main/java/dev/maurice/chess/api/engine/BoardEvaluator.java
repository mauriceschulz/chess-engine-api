package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.Piece;
import dev.maurice.chess.api.domain.Position;
import org.springframework.stereotype.Component;

@Component
public class BoardEvaluator {
    private final PieceValueProvider pieceValueProvider;

    public BoardEvaluator(PieceValueProvider pieceValueProvider) {
        this.pieceValueProvider = pieceValueProvider;
    }

    public int evaluate(Board board, Color engineColor) {
        int score = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8 ; col++) {
                Position currentPosition = new Position(row, col);
                Piece piece = board.getPiece(currentPosition);

                if (piece == null) {
                    continue;
                }

                int value = pieceValueProvider.getValue(piece.type());

                if (piece.color() == engineColor) {
                    score += value;
                } else {
                    score -= value;
                }
            }
        }
        return score;
    }
}
