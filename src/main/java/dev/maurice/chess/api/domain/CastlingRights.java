package dev.maurice.chess.api.domain;

public class CastlingRights {

    private boolean whiteKingSide;
    private boolean whiteQueenSide;
    private boolean blackKingSide;
    private boolean blackQueenSide;

    private CastlingRights(boolean whiteKingSide, boolean whiteQueenSide, boolean blackKingSide, boolean blackQueenSide) {
        this.whiteKingSide = whiteKingSide;
        this.whiteQueenSide = whiteQueenSide;
        this.blackKingSide = blackKingSide;
        this.blackQueenSide = blackQueenSide;
    }

    public static CastlingRights initial() {
        return new CastlingRights(true, true, true, true);
    }

    public CastlingRights copy() {
        return new CastlingRights(whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide);
    }

    public boolean canCastle(Color color, boolean kingSide) {
        if (color == Color.WHITE) {
            return kingSide ? whiteKingSide : whiteQueenSide;
        }

        return kingSide ? blackKingSide : blackQueenSide;
    }

    public void removeCastlingRights(Color color) {
        if (color == Color.WHITE) {
            whiteKingSide = false;
            whiteQueenSide = false;
        } else {
            blackKingSide = false;
            blackQueenSide = false;
        }
    }

    public void removeKingSide(Color color) {
        if (color == Color.WHITE) {
            whiteKingSide = false;
        } else {
            blackKingSide = false;
        }
    }

    public void removeQueenSide(Color color) {
        if (color == Color.WHITE) {
            whiteQueenSide = false;
        } else {
            blackQueenSide = false;
        }
    }
}
