package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.EngineType;
import org.springframework.stereotype.Component;

@Component
public class ChessEngineSelector {

    private final RandomMoveEngine randomMoveEngine;
    private final OnePlyEngine onePlyEngine;

    public ChessEngineSelector(RandomMoveEngine randomMoveEngine, OnePlyEngine onePlyEngine) {
        this.randomMoveEngine = randomMoveEngine;
        this.onePlyEngine = onePlyEngine;
    }

    public ChessEngine select(EngineType engineType) {
        return switch (engineType) {
            case RANDOM -> randomMoveEngine;
            case ONE_PLY -> onePlyEngine;
        };
    }
}
