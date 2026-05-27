package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.EngineType;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.GameStatus;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.engine.BoardEvaluator;
import dev.maurice.chess.api.engine.MinmaxEngine;
import dev.maurice.chess.api.engine.OpeningBook;
import dev.maurice.chess.api.engine.PieceValueProvider;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinmaxEngineTest {

    private final FakeLegalMoveGenerator legalMoveGenerator = new FakeLegalMoveGenerator();
    private final FakeBoardEvaluator boardEvaluator = new FakeBoardEvaluator();
    private final FakeOpeningBook openingBook = new FakeOpeningBook();
    private final FakeStatusGameResolver statusGameResolver = new FakeStatusGameResolver();

    private final MinmaxEngine minmaxEngine = new MinmaxEngine(
            legalMoveGenerator,
            boardEvaluator,
            openingBook,
            statusGameResolver,
            new CheckValidator(new PieceMovementValidator())
    );

    @Test
    void chooseMoveShouldPreferCheckmateOverStalemate() {
        GameSession game = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.fromFen("7k/8/6K1/5Q2/8/8/8/8"),
                Color.WHITE,
                EngineType.MINMAX
        );
        Move stalemateMove = Move.fromUci("f5g5");
        Move checkmateMove = Move.fromUci("f5f8");

        legalMoveGenerator.whenMoves(List.of(), Color.WHITE, List.of(stalemateMove, checkmateMove));
        legalMoveGenerator.whenMoves(List.of(stalemateMove.toUci()), Color.BLACK, List.of());
        legalMoveGenerator.whenMoves(List.of(checkmateMove.toUci()), Color.BLACK, List.of());
        statusGameResolver.whenStatus(List.of(stalemateMove.toUci()), GameStatus.STALEMATE);
        statusGameResolver.whenStatus(List.of(checkmateMove.toUci()), GameStatus.CHECKMATE);

        Move chosenMove = minmaxEngine.chooseMove(game);

        assertEquals(checkmateMove.toUci(), chosenMove.toUci());
    }

    @Test
    void chooseMoveShouldAvoidOpponentCheckmateResponse() {
        GameSession game = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.createInitial(),
                Color.WHITE,
                EngineType.MINMAX
        );
        Move blunderMove = Move.fromUci("e2e3");
        Move safeMove = Move.fromUci("d2d3");
        Move opponentReply = Move.fromUci("e7e5");

        legalMoveGenerator.whenMoves(List.of(), Color.WHITE, List.of(blunderMove, safeMove));
        legalMoveGenerator.whenMoves(List.of(blunderMove.toUci()), Color.BLACK, List.of(opponentReply));
        legalMoveGenerator.whenMoves(List.of(safeMove.toUci()), Color.BLACK, List.of(opponentReply));
        statusGameResolver.whenStatus(List.of(blunderMove.toUci(), opponentReply.toUci()), GameStatus.CHECKMATE);
        statusGameResolver.whenStatus(List.of(safeMove.toUci(), opponentReply.toUci()), GameStatus.ACTIVE);

        Move chosenMove = minmaxEngine.chooseMove(game);

        assertEquals(safeMove.toUci(), chosenMove.toUci());
    }

    @Test
    void chooseMoveShouldPreferCheckingMoveWhenMaterialIsEqual() {
        GameSession game = new GameSession(
                UUID.randomUUID(),
                Color.WHITE,
                Board.fromFen("4k3/8/8/8/8/8/8/4R2K"),
                Color.WHITE,
                EngineType.MINMAX
        );
        Move quietMove = Move.fromUci("e1e2");
        Move checkingMove = Move.fromUci("e1e7");

        legalMoveGenerator.whenMoves(List.of(), Color.WHITE, List.of(quietMove, checkingMove));
        legalMoveGenerator.whenMoves(List.of(quietMove.toUci()), Color.BLACK, List.of());
        legalMoveGenerator.whenMoves(List.of(checkingMove.toUci()), Color.BLACK, List.of());
        statusGameResolver.whenStatus(List.of(quietMove.toUci()), GameStatus.ACTIVE);
        statusGameResolver.whenStatus(List.of(checkingMove.toUci()), GameStatus.ACTIVE);

        Move chosenMove = minmaxEngine.chooseMove(game);

        assertEquals(checkingMove.toUci(), chosenMove.toUci());
    }

    private static class FakeLegalMoveGenerator extends LegalMoveGenerator {
        private final Map<String, List<Move>> movesByPosition = new HashMap<>();

        FakeLegalMoveGenerator() {
            super(null);
        }

        @Override
        public List<Move> generateLegalMoves(GameSession game, Color color) {
            return movesByPosition.getOrDefault(key(game.getMoveHistory(), color), List.of());
        }

        void whenMoves(List<String> history, Color color, List<Move> moves) {
            movesByPosition.put(key(history, color), moves);
        }

        private static String key(List<String> history, Color color) {
            return color.name() + ":" + String.join(",", history);
        }
    }

    private static class FakeBoardEvaluator extends BoardEvaluator {
        FakeBoardEvaluator() {
            super(new PieceValueProvider());
        }

        @Override
        public int evaluate(Board board, Color engineColor) {
            return 0;
        }
    }

    private static class FakeOpeningBook extends OpeningBook {
        @Override
        public Optional<Move> findMove(GameSession game, List<Move> legalMoves) {
            return Optional.empty();
        }
    }

    private static class FakeStatusGameResolver extends StatusGameResolver {
        private final Map<List<String>, GameStatus> statusByHistory = new HashMap<>();

        FakeStatusGameResolver() {
            super(null, null);
        }

        @Override
        public GameStatus resolve(GameSession game) {
            return statusByHistory.getOrDefault(List.copyOf(game.getMoveHistory()), GameStatus.ACTIVE);
        }

        void whenStatus(List<String> history, GameStatus status) {
            statusByHistory.put(history, status);
        }
    }
}
