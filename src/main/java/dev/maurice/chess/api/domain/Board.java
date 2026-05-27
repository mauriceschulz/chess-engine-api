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
        Piece pieceToPlace = getPiece(move.getFrom());

        if (pieceToPlace == null) {
            throw new IllegalStateException("No piece found at: " + move.getFrom().toAlgebraic());
        }

        if (pieceToPlace.type() == PieceType.KING && move.isCastlingMove()) {
            moveRookForCastling(move);
        }

        if (pieceToPlace.type() == PieceType.PAWN && move.getPromotion() != null) {
            pieceToPlace = new Piece(move.getPromotion(), pieceToPlace.color());
        }

        setPiece(move.getTo(), pieceToPlace);
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

    public static Board fromFen(String fen) {
        if (fen == null || fen.isBlank()) {
            throw new IllegalArgumentException("FEN must not be empty");
        }

        String piecePlacement = fen.trim().split("\\s+")[0];
        String[] ranks = piecePlacement.split("/", -1);

        if (ranks.length != 8) {
            throw new IllegalArgumentException("FEN must contain exactly 8 ranks: " + fen);
        }

        Board board = new Board();

        for (int row = 0; row < 8; row++) {
            int col = 0;

            for (int index = 0; index < ranks[row].length(); index++) {
                char value = ranks[row].charAt(index);

                if (Character.isDigit(value)) {
                    int emptySquares = Character.getNumericValue(value);

                    if (emptySquares < 1 || emptySquares > 8) {
                        throw new IllegalArgumentException("Invalid empty square count in FEN: " + value);
                    }

                    col += emptySquares;
                    continue;
                }

                if (col >= 8) {
                    throw new IllegalArgumentException("FEN rank contains too many squares: " + ranks[row]);
                }

                board.setPiece(
                        new Position(row, col),
                        new Piece(pieceTypeFromFenChar(value), colorFromFenChar(value))
                );
                col++;
            }

            if (col != 8) {
                throw new IllegalArgumentException("FEN rank must contain exactly 8 squares: " + ranks[row]);
            }
        }

        return board;
    }

    public Board copy() {
        Board copy = new Board();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position position = new Position(row, col);

                Piece piece = this.getPiece(position);

                copy.setPiece(position, piece);
            }
        }
        return copy;
    }

    public static void placePiece(Board board, int row, int col, PieceType type, Color color) {
        Position position = new Position(row, col);
        Piece piece = new Piece(type, color);

        board.setPiece(position, piece);
    }

    private static PieceType pieceTypeFromFenChar(char value) {
        return switch (Character.toLowerCase(value)) {
            case 'k' -> PieceType.KING;
            case 'q' -> PieceType.QUEEN;
            case 'b' -> PieceType.BISHOP;
            case 'n' -> PieceType.KNIGHT;
            case 'p' -> PieceType.PAWN;
            case 'r' -> PieceType.ROOK;
            default -> throw new IllegalArgumentException("Invalid piece in FEN: " + value);
        };
    }

    private static Color colorFromFenChar(char value) {
        return Character.isUpperCase(value) ? Color.WHITE : Color.BLACK;
    }

    private void moveRookForCastling(Move move) {
        Position kingTo = move.getTo();

        boolean kingSide = kingTo.getCol() == 6;

        int row = kingTo.getRow();

        Position rookFrom = kingSide
                ? new Position(row, 7)
                : new Position(row, 0);

        Position rookTo = kingSide
                ? new Position(row, 5)
                : new Position(row, 3);

        Piece rook = getPiece(rookFrom);

        setPiece(rookTo, rook);
        setPiece(rookFrom, null);
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
