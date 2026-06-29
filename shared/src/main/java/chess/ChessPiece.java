package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    //TODO: Do some thinking if this "first move" flag is a good implimentation
    //Stores if the piece has been moved, mostly for pawn openings
    private boolean isFirstMove;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.isFirstMove = false;
    }

    //TODO: Try to understand what these following two methods do for us.
    //VERY IMPORTANT
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return isFirstMove == that.isFirstMove && pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, isFirstMove);
    }

    //VERY IMPORTANT
    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    //Presently references ChessMoveCalculator which handles computaion and returns a sorted list of valid chess moves
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //TODO: This is a brute force override and this must be properly implimented before submission
        ChessMoveCalculator piece = new ChessMoveCalculator(board.getPiece(myPosition));

        return piece.getValidMoves(board, myPosition, this);
    }
}
