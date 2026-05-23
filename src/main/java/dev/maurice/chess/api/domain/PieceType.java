package dev.maurice.chess.api.domain;

public enum PieceType {
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT,
    PAWN;

    public static PieceType fromPromotionChar(char promotionChar) {
        return switch(Character.toLowerCase(promotionChar)) {
            case 'q' -> QUEEN;
            case 'n' -> KNIGHT;
            case 'b' -> BISHOP;
            case 'r' -> ROOK;
            default -> throw new IllegalArgumentException("Invalid promotion piece: " + promotionChar);
        };
    }

    public char toPromotionChar() {
        return switch (this) {
            case QUEEN -> 'q';
            case ROOK -> 'r';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            default -> throw new IllegalStateException("Piece cannot be used for promotion: " + this);
        };
    }
}
