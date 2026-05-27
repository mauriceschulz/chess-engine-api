package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

@Component
public class CastlingValidator {
    private final CheckValidator checkValidator;

    public CastlingValidator(CheckValidator checkValidator) {
        this.checkValidator = checkValidator;
    }

    public void validate(GameSession game, Move move) {
        Board board = game.getBoard();
        Piece king = board.getPiece(move.getFrom());
        Color color = king.color();

        validateKingStartSquare(move, color);
        validateCastlingRight(game, move);
        validateRookExists(board, move);

        if (!isPathClearForCastling(board, move)) {
            throw new InvalidMoveException("Castling path is blocked");
        }

        validateKingSafety(board, move, color);
    }

    private boolean isKingSide(Move move) {
        return move.getTo().getCol() > move.getFrom().getCol();
    }

    private Position getRookPosition(Move move) {
        int row = move.getFrom().getRow();
        int col = isKingSide(move) ? 7 : 0;

        return new Position(row, col);
    }

    private Position getTransitSquare(Move move) {
        int row = move.getFrom().getRow();
        int col = isKingSide(move) ? 5 : 3;

        return new Position(row, col);
    }

    private boolean isPathClearForCastling(Board board, Move move) {
        Position from = move.getFrom();
        Position rookPosition = getRookPosition(move);
        int colStep = Integer.compare(rookPosition.getCol(), from.getCol());

        int currentCol = from.getCol() + colStep;

        while (currentCol != rookPosition.getCol()) {
            Position current = new Position(from.getRow(), currentCol);

            if (board.getPiece(current) != null) {
                return false;
            }

            currentCol += colStep;
        }

        return true;
    }

    private void validateCastlingRight(GameSession game, Move move) {
        Color color = game.getSideToMove();

        if (!game.getCastlingRights().canCastle(color, isKingSide(move))) {
            throw new InvalidMoveException("Castling right is no longer available");
        }
    }

    private void validateRookExists(Board board, Move move) {
        Piece king = board.getPiece(move.getFrom());
        Piece rook = board.getPiece(getRookPosition(move));

        if (rook == null || rook.type() != PieceType.ROOK || rook.color() != king.color()) {
            throw new InvalidMoveException("Castling rook is missing");
        }
    }

    private void validateKingSafety(Board board, Move move, Color color) {
        if (checkValidator.isKingInCheck(board, color)) {
            throw new InvalidMoveException("Cannot castle while king is in check");
        }

        validateKingDoesNotPassThroughAttackedSquare(board, move, color);

        Board castledBoard = board.copy();
        castledBoard.movePiece(move);

        if (checkValidator.isKingInCheck(castledBoard, color)) {
            throw new InvalidMoveException("Cannot castle into check");
        }
    }

    private void validateKingStartSquare(Move move, Color color) {
        int expectedRow = color == Color.WHITE ? 7 : 0;

        if (move.getFrom().getRow() != expectedRow
                || move.getFrom().getCol() != 4
                || move.getTo().getRow() != expectedRow
                || (move.getTo().getCol() != 6 && move.getTo().getCol() != 2)) {
            throw new InvalidMoveException("Invalid castling move");
        }
    }

    private void validateKingDoesNotPassThroughAttackedSquare(Board board, Move move, Color color) {
        Board transitBoard = board.copy();
        Piece king = transitBoard.getPiece(move.getFrom());

        transitBoard.setPiece(move.getFrom(), null);
        transitBoard.setPiece(getTransitSquare(move), king);

        if (checkValidator.isKingInCheck(transitBoard, color)) {
            throw new InvalidMoveException("Cannot castle through check");
        }
    }
}
