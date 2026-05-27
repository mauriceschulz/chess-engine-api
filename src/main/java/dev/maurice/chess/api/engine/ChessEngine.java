package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;

public interface ChessEngine {

    Move chooseMove(GameSession game);
}
