package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.rules.LegalMoveGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MinmaxEngine implements ChessEngine {
    private final LegalMoveGenerator legalMoveGenerator;
    private final BoardEvaluator boardEvaluator;
    private final Random random = new Random();

    public MinmaxEngine(LegalMoveGenerator legalMoveGenerator, BoardEvaluator boardEvaluator) {
        this.legalMoveGenerator = legalMoveGenerator;
        this.boardEvaluator = boardEvaluator;
    }

    @Override
    public Move chooseMove(GameSession game) {

        Color engineColor = game.getSideToMove();

        List<Move> engineMoves =
                legalMoveGenerator.generateLegalMoves(
                        game,
                        engineColor
                );

        if (engineMoves.isEmpty()) {
            throw new IllegalStateException("No legal moves available");
        }

        List<Move> bestMoves = new ArrayList<>();

        int bestScore = Integer.MIN_VALUE;

        for (Move engineMove : engineMoves) {

            GameSession afterEngineMove = game.copy();

            afterEngineMove.applyMove(engineMove);

            List<Move> opponentMoves =
                    legalMoveGenerator.generateLegalMoves(
                            afterEngineMove,
                            afterEngineMove.getSideToMove()
                    );

            int worstScoreForEngine = Integer.MAX_VALUE;

            if (opponentMoves.isEmpty()) {

                worstScoreForEngine =
                        boardEvaluator.evaluate(
                                afterEngineMove.getBoard(),
                                engineColor
                        );

            } else {

                for (Move opponentMove : opponentMoves) {

                    GameSession afterOpponentMove =
                            afterEngineMove.copy();

                    afterOpponentMove.applyMove(opponentMove);

                    int score =
                            boardEvaluator.evaluate(
                                    afterOpponentMove.getBoard(),
                                    engineColor
                            );

                    if (score < worstScoreForEngine) {
                        worstScoreForEngine = score;
                    }
                }
            }

            if (worstScoreForEngine > bestScore) {

                bestScore = worstScoreForEngine;

                bestMoves.clear();
                bestMoves.add(engineMove);

            } else if (worstScoreForEngine == bestScore) {

                bestMoves.add(engineMove);
            }
        }

        return bestMoves.get(
                random.nextInt(bestMoves.size())
        );
    }
}
