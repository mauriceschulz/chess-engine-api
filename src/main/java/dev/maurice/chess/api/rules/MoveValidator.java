package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

@Component
public class MoveValidator {

    private final PieceMovementValidator pieceMovementValidator;
    private final CheckValidator checkValidator;
    private final CastlingValidator castlingValidator;

    public MoveValidator(PieceMovementValidator pieceMovementValidator, CheckValidator checkValidator, CastlingValidator castlingValidator) {
        this.pieceMovementValidator = pieceMovementValidator;
        this.checkValidator = checkValidator;
        this.castlingValidator = castlingValidator;
    }


    public void validate(GameSession game, Move move) {
        validateDifferentSquares(move);
        validatePieceExists(game, move);
        validateCorrectTurn(game, move);
        validateTargetIsNotOwnPiece(game, move);

        Board board = game.getBoard();
        Piece piece = board.getPiece(move.getFrom());

        if (piece.type() == PieceType.KING && move.isCastlingMove()) {
            castlingValidator.validate(game, move);
            return;
        }

        if (piece.type() == PieceType.PAWN && isEnPassantMove(game, move, piece)) {
            checkValidator.validateKingSafety(game, move);
            return;
        }

        pieceMovementValidator.validate(game.getBoard(), move, game.getBoard().getPiece(move.getFrom()));
        checkValidator.validateKingSafety(game, move);
    }

    private boolean isEnPassantMove(GameSession game, Move move, Piece piece) {
        Position from = move.getFrom();
        Position to = move.getTo();
        int direction = piece.color().isWhite() ? -1 : 1;
        int requiredFromRow = piece.color().isWhite() ? 3 : 4;

        if (from.getRow() != requiredFromRow
                || to.getRow() - from.getRow() != direction
                || Math.abs(to.getCol() - from.getCol()) != 1
                || game.getBoard().getPiece(to) != null
                || game.getMoveHistory().isEmpty()) {
            return false;
        }

        Move lastMove = Move.fromUci(game.getMoveHistory().getLast());
        Piece capturedPawn = game.getBoard().getPiece(lastMove.getTo());

        return capturedPawn != null
                && capturedPawn.type() == PieceType.PAWN
                && capturedPawn.color() == piece.color().opposite()
                && lastMove.getFrom().getCol() == lastMove.getTo().getCol()
                && Math.abs(lastMove.getFrom().getRow() - lastMove.getTo().getRow()) == 2
                && lastMove.getTo().getRow() == from.getRow()
                && lastMove.getTo().getCol() == to.getCol();
    }

    private void validateDifferentSquares(Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();

        if (from.getRow() == to.getRow()
                && from.getCol() == to.getCol()) {

            throw new InvalidMoveException("Move cannot contain identical squares");
        }
    }

    private void validatePieceExists(GameSession game, Move move) {
        Piece pieceToMove = game.getBoard().getPiece(move.getFrom());

        if (pieceToMove == null) {
            throw  new InvalidMoveException("Move cannot include empty square");
        }
    }

    private void validateCorrectTurn(GameSession game, Move move) {
        Color expectedColor = game.getSideToMove();
        Piece pieceToMove = game.getBoard().getPiece(move.getFrom());

        if (expectedColor != pieceToMove.color()) {
            throw  new InvalidMoveException("Trying to move a piece of the non active player");
        }

    }

    private void validateTargetIsNotOwnPiece(GameSession game, Move move) {
        Color colorToMove = game.getSideToMove();
        Piece pieceOnTargetSquare = game.getBoard().getPiece(move.getTo());

        if (pieceOnTargetSquare != null && pieceOnTargetSquare.color() == colorToMove) {
            throw  new InvalidMoveException("Move cannot target a Square with the players piece");
        }
    }
}
