package dev.maurice.chess.api.domain;

public class Move {
    private final Position from;
    private final Position to;
    private final PieceType promotion;

    public Move(Position from, Position to) {
        this (from, to ,null);
    }

    public Move(Position from, Position to, PieceType promotion) {
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
}