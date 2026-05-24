package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.domain.Piece;
import dev.maurice.chess.api.exception.InvalidMoveException;

public class PieceMovementValidator {

    public void validate(Board board, Move move, Piece piece) {
        boolean valid = switch (piece.type()) {
            case PAWN -> isValidPawnMove(board, move, piece);
            case KNIGHT -> isValidKnightMove(move);
            case BISHOP -> isValidBishopMove(board, move);
            case ROOK -> isValidRookMove(board, move);
            case QUEEN -> isValidQueenMove(board, move);
            case KING -> isValidKingMove(move);
        };

        if (!valid) {
            throw new InvalidMoveException("Illegal move for " + piece.type());
        }
    }

    private boolean isValidKnightMove(Move move) {

        return true;
    }

    private boolean isValidPawnMove(Board board, Move move, Piece piece) {
        return true;
    }

    private boolean isValidBishopMove(Board board, Move move) {
        return true;
    }

    private boolean isValidRookMove(Board board, Move move) {
        return true;
    }

    private boolean isValidQueenMove(Board board, Move move) {
        return true;
    }

    private boolean isValidKingMove(Move move) {
        return true;
    }
}
