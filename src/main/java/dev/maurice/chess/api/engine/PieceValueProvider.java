package dev.maurice.chess.api.engine;

import dev.maurice.chess.api.domain.PieceType;
import org.springframework.stereotype.Component;

@Component
public class PieceValueProvider {
    public int getValue(PieceType pieceType) {
        return switch (pieceType){
            case PAWN -> 100;
            case KNIGHT -> 320;
            case BISHOP -> 330;
            case ROOK -> 500;
            case QUEEN -> 900;
            case KING -> 0;
        };
    }

}
