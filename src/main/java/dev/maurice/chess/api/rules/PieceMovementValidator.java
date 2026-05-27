package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.Board;
import dev.maurice.chess.api.domain.Move;
import dev.maurice.chess.api.domain.Piece;
import dev.maurice.chess.api.domain.Position;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

@Component
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
        Position from = move.getFrom();
        Position to = move.getTo();

        int rowDelta = Math.abs(from.getRow() - to.getRow());
        int colDelta = Math.abs(from.getCol() - to.getCol());

        return (rowDelta == 1 && colDelta == 2)
                || (rowDelta == 2 && colDelta ==1);
    }

    private boolean isValidPawnMove(Board board, Move move, Piece piece) {
        Position from = move.getFrom();
        Position to = move.getTo();

        int rowDelta = to.getRow() - from.getRow();
        int colDelta = to.getCol() - from.getCol();
        int direction = piece.color().isWhite() ? -1 : 1;
        int startRow = piece.color().isWhite() ? 6 : 1;

        Piece target = board.getPiece(to);

        boolean reachesPromotionRank =
                piece.color().isWhite()
                        ? to.getRow() == 7
                        : to.getRow() == 0;

        if (reachesPromotionRank && move.getPromotion() == null) {
            return false;
        }

        if (!reachesPromotionRank && move.getPromotion() != null) {
            return false;
        }

        if (colDelta == 0) {
            if (target != null) {
                return false;
            }

            if (rowDelta == direction) {
                return true;
            }

            if (from.getRow() == startRow && rowDelta == 2 * direction) {
                Position between = new Position(from.getRow() + direction, from.getCol());
                if (board.getPiece(between) == null) {
                    return true;
                }
            }
        }

        // TODO: Add en passant support for diagonal pawn moves to empty squares.
        if (Math.abs(colDelta) == 1 && rowDelta == direction) {
            return target != null && target.color() != piece.color();
        }

        return false;
    }

    private boolean isValidBishopMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        int rowDelta = Math.abs(from.getRow() - to.getRow());
        int colDelta = Math.abs(from.getCol() - to.getCol());

        if (rowDelta != colDelta) return false;

        return isPathClear(board, move);
    }

    private boolean isValidRookMove(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        boolean sameRow = from.getRow() == to.getRow();
        boolean sameCol = from.getCol() == to.getCol();

        if (!sameRow && !sameCol) return false;

        return isPathClear(board, move);
    }

    private boolean isValidQueenMove(Board board, Move move) {
        return isValidRookMove(board, move)
                || isValidBishopMove(board, move);
    }

    private boolean isValidKingMove(Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        int rowDelta = Math.abs(from.getRow() - to.getRow());
        int colDelta = Math.abs(from.getCol() - to.getCol());

        return rowDelta <= 1 && colDelta <= 1;
    }

    private boolean isPathClear(Board board, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());

        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;

        while (currentRow != to.getRow()
                || currentCol != to.getCol()) {

            Position current =
                    new Position(currentRow, currentCol);

            if (board.getPiece(current) != null) {
                return false;
            }

            currentRow += rowStep;
            currentCol += colStep;
        }

        return true;
    }
}
