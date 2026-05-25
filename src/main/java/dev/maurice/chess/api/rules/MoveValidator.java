package dev.maurice.chess.api.rules;

import dev.maurice.chess.api.domain.*;
import dev.maurice.chess.api.exception.InvalidMoveException;
import org.springframework.stereotype.Component;

@Component
public class MoveValidator {

    private final PieceMovementValidator pieceMovementValidator;

    public MoveValidator(PieceMovementValidator pieceMovementValidator) {
        this.pieceMovementValidator = pieceMovementValidator;
    }


    public void validate(GameSession game, Move move) {
        validateDifferentSquares(move);
        validatePieceExists(game, move);
        validateCorrectTurn(game, move);
        validateTargetIsNotOwnPiece(game, move);
        pieceMovementValidator.validate(game.getBoard(), move, game.getBoard().getPiece(move.getFrom()));
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
