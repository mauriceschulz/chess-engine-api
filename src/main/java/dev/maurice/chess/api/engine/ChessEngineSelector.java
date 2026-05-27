package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.EngineType;
import org.springframework.stereotype.Component;

@Component
public class ChessEngineSelector {

    private final RandomMoveEngine randomMoveEngine;
    private final OnePlyEngine onePlyEngine;
    private final MinmaxEngine minmaxEngine;

    public ChessEngineSelector(RandomMoveEngine randomMoveEngine, OnePlyEngine onePlyEngine, MinmaxEngine minmaxEngine) {
        this.randomMoveEngine = randomMoveEngine;
        this.onePlyEngine = onePlyEngine;
        this.minmaxEngine = minmaxEngine;
    }

    public ChessEngine select(EngineType engineType) {
        return switch (engineType) {
            case RANDOM -> randomMoveEngine;
            case ONE_PLY -> onePlyEngine;
            case MINMAX -> minmaxEngine;
        };
    }
}
