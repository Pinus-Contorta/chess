package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessMove lastMoveMade;

    private boolean whiteKingsideCastle = true;
    private boolean whiteQueensideCastle = true;
    private boolean blackKingsideCastle = true;
    private boolean blackQueensideCastle = true;

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        this.lastMoveMade = null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if(inBounds(position.getRow(), position.getColumn())) {
            squares[position.getRow() - 1][position.getColumn() - 1] = piece;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
            ChessPiece.PieceType[] backRank = {
                    ChessPiece.PieceType.ROOK,
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.QUEEN,
                    ChessPiece.PieceType.KING,
                    ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.ROOK


            };

            for (int col = 1; col <= 8; col++) {
                ChessPiece.PieceType type = backRank[col - 1];   // array is 0-indexed, board is 1-indexed

                // Back ranks: white on 1, black on 8
                addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, type));
                addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, type));

                // Pawn ranks: white on 2, black on 7
                addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            }

            //To enable castling by making sure each of the pieces have their isFirstMove flag reset on a board reset.
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPiece piece = getPiece(new ChessPosition(row, col));
                    if (piece != null) {
                        piece.setFirstMove(true);
                    }
                }
            }

    }

    public boolean inBounds(int row, int col) {
        return (row >= 1 && row <= squares.length) && (col >= 1 && col <= squares.length);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public Object clone() {
        try {
            ChessBoard copy = (ChessBoard) super.clone();
            copy.squares = new ChessPiece[squares.length][squares[0].length];
            for (int row = 0; row < squares.length; row++) {
                for (int col = 0; col < squares[row].length; col++) {
                    copy.squares[row][col] = squares[row][col]; // or deep-copy the piece too, if ChessPiece is mutable
                }
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }


    public ChessMove getLastMoveMade() {
        return lastMoveMade;
    }

    public void setLastMoveMade(ChessMove lastMoveMade) {
        this.lastMoveMade = lastMoveMade;
    }

    public boolean canCastleKingside(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE ? whiteKingsideCastle : blackKingsideCastle;
    }

    public boolean canCastleQueenside(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE ? whiteQueensideCastle : blackQueensideCastle;
    }

    public void disableCastleKingside(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {whiteKingsideCastle = false;}
        else {blackKingsideCastle = false;}
    }

    public void disableCastleQueenside(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {whiteQueensideCastle = false;}
        else {blackQueensideCastle = false;}
    }
}
