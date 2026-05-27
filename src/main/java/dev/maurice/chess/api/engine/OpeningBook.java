package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.Move;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Component
public class OpeningBook {
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    private final Random random = new Random();

    private final Map<List<String>, List<String>> movesByHistory = Map.ofEntries(
            Map.entry(List.of(), List.of("e2e4", "d2d4", "c2c4", "g1f3")),

            Map.entry(List.of("e2e4"), List.of("c7c5", "e7e5", "e7e6", "c7c6")),
            Map.entry(List.of("d2d4"), List.of("d7d5", "g8f6", "e7e6", "c7c6")),
            Map.entry(List.of("c2c4"), List.of("g8f6", "e7e5", "c7c5", "e7e6")),
            Map.entry(List.of("g1f3"), List.of("d7d5", "g8f6", "c7c5")),

            Map.entry(List.of("e2e4", "e7e5"), List.of("g1f3", "f1c4", "b1c3")),
            Map.entry(List.of("e2e4", "c7c5"), List.of("g1f3", "b1c3", "d2d4")),
            Map.entry(List.of("e2e4", "e7e6"), List.of("d2d4", "g1f3")),
            Map.entry(List.of("e2e4", "c7c6"), List.of("d2d4", "g1f3")),
            Map.entry(List.of("d2d4", "d7d5"), List.of("c2c4", "g1f3")),
            Map.entry(List.of("d2d4", "g8f6"), List.of("c2c4", "g1f3", "c1g5")),

            Map.entry(List.of("e2e4", "e7e5", "g1f3"), List.of("b8c6", "g8f6")),
            Map.entry(List.of("e2e4", "c7c5", "g1f3"), List.of("d7d6", "b8c6", "e7e6")),
            Map.entry(List.of("d2d4", "d7d5", "c2c4"), List.of("e7e6", "c7c6", "d5c4")),
            Map.entry(List.of("d2d4", "g8f6", "c2c4"), List.of("e7e6", "g7g6", "c7c5"))
    );

    public Optional<Move> findMove(GameSession game, List<Move> legalMoves) {
        if (game.getMoveHistory().isEmpty() && !game.getBoard().toFen().equals(STARTING_FEN)) {
            return Optional.empty();
        }

        List<String> bookMoves = movesByHistory.get(game.getMoveHistory());

        if (bookMoves == null || bookMoves.isEmpty()) {
            return Optional.empty();
        }

        List<Move> legalBookMoves = legalMoves.stream()
                .filter(move -> bookMoves.contains(move.toUci()))
                .toList();

        if (legalBookMoves.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(legalBookMoves.get(random.nextInt(legalBookMoves.size())));
    }
}
