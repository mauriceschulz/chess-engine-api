package dev.maurice.chess.api.domain;

public class Board {
    private final Piece[][] squares;

    public Board() {
        this.squares = new Piece[8][8];
    }

    public Piece getPiece(Position position) {
        if (!position.isValid()) {
            throw new IllegalArgumentException("Position is not valid: " + position.toAlgebraic());
        }

        return squares[position.getRow()][position.getCol()];
    }

    public void setPiece(Position position, Piece piece) {
        if (!position.isValid()) {
            throw new IllegalArgumentException("Position is not valid: " + position.toAlgebraic());
        }

        squares[position.getRow()][position.getCol()] = piece;
    }

    public void movePiece(Move move) {
        Piece piece = getPiece(move.getFrom());

        if (piece == null) {
            throw new IllegalStateException("No piece found at: " + move.getFrom().toAlgebraic());
        }

        setPiece(move.getTo(), piece);
        setPiece(move.getFrom(), null);
    }

    public static Board createInitial() {
        return null;
    }

    public String toFen() {
        return "";
    }
}