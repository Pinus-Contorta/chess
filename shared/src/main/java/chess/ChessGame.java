package chess;

import java.util.ArrayList;
import java.util.Collection;
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

        board = new ChessBoard();

        board.resetBoard();
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

        if (board.getPiece(startPosition) != null) {
            Collection<ChessMove> candidateMoves = new ChessMoveCalculator(board.getPiece(startPosition)).getValidMoves(board, startPosition, board.getPiece(startPosition));

            ChessPiece movingPiece = board.getPiece(startPosition);
            TeamColor teamColor = movingPiece.getTeamColor();

            Collection<ChessMove> safeMoves = new ArrayList<>();

            for (ChessMove move : candidateMoves) {
                //I need a board to actually move a few pieces on to see if moving pieces, not just the king, will be valid.
                ChessBoard clonedBoard = (ChessBoard) board.clone();

                ChessPiece pieceToMove = clonedBoard.getPiece(move.getStartPosition());
                clonedBoard.addPiece(move.getEndPosition(), pieceToMove);
                clonedBoard.addPiece(move.getStartPosition(), null);

                if (!wouldBeInCheck(teamColor, clonedBoard)) {
                    safeMoves.add(move);
                }
            }

            return safeMoves;
        } else {
            return null;
        }
    }

    private boolean wouldBeInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPosition = findKing(board, teamColor);

        for (int row = 1; row <= board.squares.length; row++) {
            for (int col = 1; col <= board.squares.length; col++) {
                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> targetMoves = new ChessMoveCalculator(targetPiece).getValidMoves(board, targetPosition, targetPiece);

                    for (ChessMove enemyMove : targetMoves) {
                        if (enemyMove.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (this.board != null) {
            ChessPiece movingPiece = board.getPiece(move.getStartPosition());

            int startRow = move.getStartPosition().getRow();
            int startCol = move.getStartPosition().getColumn();

            int endRow = move.getEndPosition().getRow();
            int endCol = move.getEndPosition().getColumn();

            if (validMoves(move.getStartPosition()).contains(move)) {

                //Sets end position to data of movingPiece
                board.squares[endRow][endCol] = movingPiece;

                //Sets point of origin to null
                board.squares[startRow][endRow] = null;
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPosition = findKing(board, teamColor);

        for (int row = 1; row <= board.squares.length; row++) {

            for (int col = 1; col <= board.squares.length; col++) {

                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                if ((board.getPiece(targetPosition) != null) && (targetPiece.getTeamColor() != teamColor)){
                    Collection<ChessMove> targetMoves = new ChessMoveCalculator(targetPiece).getValidMoves(board, targetPosition, targetPiece);

                    for (ChessMove move : targetMoves){

                        if (move.getEndPosition().equals(kingPosition)) {
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
        ChessPosition kingPosition = findKing(board, teamColor);

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

    public ChessPosition findKing(ChessBoard board, ChessGame.TeamColor teamColor) {
        ChessPosition kingPosition = null;

        for (int row = 1; row <= board.squares.length; row++) {

            for(int col = 1; col <= board.squares.length; col++) {

                ChessPiece targetPiece = board.getPiece(new ChessPosition(row, col));

                if ((targetPiece != null) && (targetPiece.getTeamColor() == teamColor) && (targetPiece.getPieceType() == ChessPiece.PieceType.KING)) {
                    kingPosition = new ChessPosition(row,col);
                }
            }
        }
        return kingPosition;
    }
}
