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
        return new Move(null, null, null);
    }

    public String toUci() {
        return "";
    }
}