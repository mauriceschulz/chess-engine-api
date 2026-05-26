package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.rules.LegalMoveGenerator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomMoveEngine {
    private final LegalMoveGenerator legalMoveGenerator;
    Random random = new Random();

    public RandomMoveEngine(LegalMoveGenerator legalMoveGenerator) {
        this.legalMoveGenerator = legalMoveGenerator;
    }

    public Move chooseMove(GameSession game) {
        List<Move> legalMoves = legalMoveGenerator.generateLegalMoves(game, game.getSideToMove());

        if (legalMoves.isEmpty()) {
            throw new IllegalStateException("Cannot generate legal move in this position");
        }

        int randomIndex = random.nextInt(legalMoves.size());

        return legalMoves.get(randomIndex);
    }
}
