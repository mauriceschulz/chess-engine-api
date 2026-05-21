package dev.maurice.chess.api.domain;

public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public static Position fromAlgebraic(String value) {
        value = value.toLowerCase();

        int col = value.charAt(0) - 'a';

        int rank = Character.getNumericValue(value.charAt(1));
        int row = 8 - rank;

        return new Position(row, col);
    }

    public String toAlgebraic() {

        char file = (char) ('a' + this.col);
        char rank = (char) ('8' - this.row);

        return "" + file + rank;
    }

    public boolean isValid() {
        return this.col >= 0
                && this.col < 8
                && this.row >= 0
                && this.row <8;
    }
}