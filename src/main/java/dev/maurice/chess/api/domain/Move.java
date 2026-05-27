package dev.maurice.chess.api.domain;

import dev.maurice.chess.api.exception.InvalidMoveException;

public class Move {
    private final Position from;
    private final Position to;
    private final PieceType promotion;

    public Move(Position from, Position to) {
        this (from, to ,null);
    }

    public Move(Position from, Position to, PieceType promotion) {
        if (from == null || to == null) {
            throw new InvalidMoveException("Move must contain a source and a destination");
        }

        if (promotion == PieceType.KING || promotion == PieceType.PAWN) {
            throw new InvalidMoveException("Invalid promotion piece: " + promotion);
        }

        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public PieceType getPromotion() {
        return promotion;
    }

    public static Move fromUci(String value) {
        if (value == null || !value.matches("[a-hA-H][1-8][a-hA-H][1-8][qQrRnNbB]?")) {
            throw new InvalidMoveException("Invalid move: " + value);
        }

        Position from = Position.fromAlgebraic(value.substring(0, 2));
        Position to = Position.fromAlgebraic(value.substring(2, 4));

        if (value.length() == 5) {
            PieceType promotion = PieceType.fromPromotionChar(value.charAt(4));
            return new Move(from, to, promotion);
        }

        return new Move(from, to, null);
    }

    public String toUci() {
        String uci = this.from.toAlgebraic()
                + this.to.toAlgebraic();

        if (this.promotion != null) {
            uci += this.promotion.toPromotionChar();
        }

        return uci;

    }

    public boolean isCastlingMove() {
        int rowDelta = Math.abs(from.getRow() - to.getRow());
        int colDelta = Math.abs(from.getCol() - to.getCol());

        return rowDelta == 0 && colDelta == 2;
    }
}
