package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Color;
import dev.maurice.chess.api.domain.GameSession;
import dev.maurice.chess.api.domain.GameStatus;
import org.springframework.stereotype.Component;

@Component
public class StatusGameResolver {

    private final LegalMoveGenerator legalMoveGenerator;
    private final CheckValidator checkValidator;


    public StatusGameResolver(LegalMoveGenerator legalMoveGenerator, CheckValidator checkValidator) {
        this.legalMoveGenerator = legalMoveGenerator;
        this.checkValidator = checkValidator;
    }

    public GameStatus resolve (GameSession game) {
        Color sideToMove = game.getSideToMove();

        boolean hasLegalMove = !legalMoveGenerator.generateLegalMoves(game, sideToMove).isEmpty();

        if (hasLegalMove) {
            return GameStatus.ACTIVE;
        }

        boolean inCheck = checkValidator.isKingInCheck(game.getBoard(), sideToMove);

        if (inCheck) {
            return GameStatus.CHECKMATE;
        }

        return GameStatus.STALEMATE;
    }
}
