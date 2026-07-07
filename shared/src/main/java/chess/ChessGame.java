package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    public TeamColor teamTurn;

    public ChessGame() {

        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> initMoveSet = new ChessMoveCalculator(board.getPiece(startPosition)).getValidMoves(board, startPosition, board.getPiece(startPosition));
        Collection<ChessMove> validMoveSet = initMoveSet;

        return validMoveSet;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPosition = findKing(teamColor);

        for (int row = 1; row <= board.squares.length; row++) {

            for (int col = 1; col <= board.squares.length; col++) {

                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                if ((board.getPiece(targetPosition) != null) && (targetPiece.getTeamColor() != teamColor)){
                    Collection<ChessMove> targetMoves = new ChessMoveCalculator(targetPiece).getValidMoves(board, targetPosition, targetPiece);

                    for (ChessMove move : targetMoves){

                        if (move.getEndPosition() == kingPosition) {
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);

        if (validMoves(kingPosition).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }

    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        ChessPosition king = null;

        for (int row = 1; row <= board.squares.length; row++) {

            for(int col = 1; col <= board.squares.length; col++) {

                ChessPiece targetPiece = board.getPiece(new ChessPosition(row, col));

                if ((targetPiece != null) && (targetPiece.getTeamColor() == teamColor) && (targetPiece.getPieceType() == ChessPiece.PieceType.KING)) {
                    king = new ChessPosition(row,col);
                }
            }
        }
        return king;
    }
}
