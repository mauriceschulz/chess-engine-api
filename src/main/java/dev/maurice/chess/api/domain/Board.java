package dev.maurice.chess.api.domain;

public class Board {
    private final Piece[][] squares;

    private Board() {
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
        Board board = new Board();

        PieceType[] backRank = {
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN,
                PieceType.KING,
                PieceType.BISHOP,
                PieceType.KNIGHT,
                PieceType.ROOK
        };

        for (int col = 0; col < 8; col++) {
            placePiece(board, 0, col, backRank[col], Color.BLACK);
            placePiece(board, 1, col, PieceType.PAWN, Color.BLACK);

            placePiece(board, 6, col, PieceType.PAWN, Color.WHITE);
            placePiece(board, 7, col, backRank[col], Color.WHITE);
        }
        return board;
    }

    public static void placePiece(Board board, int row, int col, PieceType type, Color color) {
        Position position = new Position(row, col);
        Piece piece = new Piece(type, color);

        board.setPiece(position, piece);
    }

    private char toFenChar(Piece piece) {
        char value = switch (piece.type()) {
            case KING -> 'k';
            case QUEEN -> 'q';
            case BISHOP -> 'b';
            case KNIGHT -> 'n';
            case PAWN -> 'p';
            case ROOK -> 'r';
        };

        if (piece.color() == Color.WHITE) {
            value = Character.toUpperCase(value);
        }

        return value;
    }

    public String toFen() {

        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;

            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];

                if (piece == null) {
                    emptyCount ++;
                    continue;
                }

                if (emptyCount > 0) {
                    fen.append(emptyCount);
                    emptyCount = 0;
                }

                fen.append(toFenChar(piece));
            }

            if (emptyCount > 0) {
                fen.append(emptyCount);
            }

            if (row < 7) {
                fen.append("/");
            }
        }

        return fen.toString();
    }
}