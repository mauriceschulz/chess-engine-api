package dev.maurice.chess.api.domain;

public enum Color {
    WHITE,
    BLACK;

    public Color opposite() {return this == WHITE ? BLACK : WHITE;}

    public boolean isWhite() {return this == WHITE;}

    public boolean isBlack() {return this == BLACK;}

    @Override
    public String toString() {
        return switch (this) {
            case WHITE -> "WHITE";
            case BLACK -> "BLACK";
        };
    }
}