package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.rules.LegalMoveGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OnePlyEngine implements ChessEngine {
    private final LegalMoveGenerator legalMoveGenerator;
    private final BoardEvaluator boardEvaluator;

    public OnePlyEngine(LegalMoveGenerator legalMoveGenerator, BoardEvaluator boardEvaluator) {
        this.legalMoveGenerator = legalMoveGenerator;
        this.boardEvaluator = boardEvaluator;
    }

    @Override
    public Move chooseMove(GameSession game) {
        Color engineColor = game.getSideToMove();
        List<Move> legalMoves = legalMoveGenerator.generateLegalMoves(game, engineColor);

        if (legalMoves.isEmpty()) {
            throw new IllegalStateException("Cannot generate legal move in this position");
        }

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : legalMoves) {
            Board copy = game.getBoard().copy();
            copy.movePiece(move);

            int score = boardEvaluator.evaluate(copy, engineColor);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }
}
