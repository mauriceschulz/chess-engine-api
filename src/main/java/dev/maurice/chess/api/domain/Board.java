package dev.maurice.chess.api.domain;

public class Board {
    private final Piece[][] squares;

    public Board() {
        this.squares = new Piece[8][8];
    }

    public Piece getPiece(Position position) {
        return null;
    }

    public void setPiece(Position position, Piece piece) {

    }

    public void movePiece(Move move) {

    }

    public static Board createInitial() {
        return null;
    }

    public String toFen() {
        return "";
    }
}