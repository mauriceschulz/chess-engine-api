package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

@Component
public class CheckValidator {
    private final PieceMovementValidator pieceMovementValidator;

    public CheckValidator(PieceMovementValidator pieceMovementValidator) {
        this.pieceMovementValidator = pieceMovementValidator;
    }

    public Position findKing(Board board, Color color) {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position currentPosition = new Position(row, col);

                Piece piece = board.getPiece(currentPosition);

                if (piece != null
                        && piece.type() == PieceType.KING
                        && piece.color() == color)
                    return currentPosition;
            }
        }
        throw new IllegalStateException("Board has no King with color: " + color);
    }

    private boolean isSquareAttacked(Board board, Position square, Color byColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position currentPosition = new Position(row, col);
                Piece currentPiece = board.getPiece(currentPosition);

                if (currentPiece == null || currentPiece.color() != byColor) continue;

                Move move = new Move(currentPosition, square);
                try {
                    pieceMovementValidator.validate(board, move, currentPiece);
                    return true;
                } catch (InvalidMoveException ignored) {
                    //The current piece cannot attack the square
                }
            }
        }
        return false;
    }

    private boolean isKingInCheck(Board board, Color kingColor) {
        Position kingPosition = findKing(board, kingColor);
        return isSquareAttacked(board, kingPosition, kingColor.opposite());
    }


    public void validateKingSafety(GameSession game, Move move) {
        Board simulatedBoard = game.getBoard().copy();

        simulatedBoard.movePiece(move);

        if (isKingInCheck(simulatedBoard, game.getSideToMove())) {
            throw new InvalidMoveException("Move would leave king in check");
        }
    }
}