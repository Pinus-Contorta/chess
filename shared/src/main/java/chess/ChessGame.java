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

                boolean isDiagonalMove =
                        move.getStartPosition().getColumn() != move.getEndPosition().getColumn();
                boolean isPawnMove = pieceToMove.getPieceType() == ChessPiece.PieceType.PAWN;
                boolean destinationEmpty = clonedBoard.getPiece(move.getEndPosition()) == null;
                boolean isEnPassantCapture = isPawnMove && isDiagonalMove && destinationEmpty;

                boolean isCastle = pieceToMove.getPieceType() == ChessPiece.PieceType.KING && Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2;


                clonedBoard.addPiece(move.getEndPosition(), pieceToMove);
                clonedBoard.addPiece(move.getStartPosition(), null);


                //For En Passant
                if (isEnPassantCapture) {
                    ChessPosition capturedPawnPosition =
                            new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                    clonedBoard.addPiece(capturedPawnPosition, null);
                }

                //For Castling
                if (isCastle) {
                    int row = move.getStartPosition().getRow();
                    boolean kingside = move.getEndPosition().getColumn() == 7;

                    int rookStartCol = kingside ? 8 : 1;
                    int rookEndCol = kingside ? 6 : 4;

                    ChessPosition rookStart = new ChessPosition(row, rookStartCol);
                    ChessPosition rookEnd = new ChessPosition(row, rookEndCol);

                    ChessPiece rook = clonedBoard.getPiece(rookStart);
                    clonedBoard.addPiece(rookEnd, rook);
                    clonedBoard.addPiece(rookStart, null);
                }

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
            if(movingPiece != null && movingPiece.getTeamColor() == teamTurn) {

                if (validMoves(move.getStartPosition()).contains(move)) {

                    boolean isDiagonalMove = move.getStartPosition().getColumn() != move.getEndPosition().getColumn();
                    boolean isPawnMove = movingPiece.getPieceType() == ChessPiece.PieceType.PAWN;
                    boolean destinationEmpty = board.getPiece(move.getEndPosition()) == null;

                    boolean isEnPassantCapture = isPawnMove && isDiagonalMove && destinationEmpty;

                    //Sets end position to data of movingPiece
                    if(move.getPromotionPiece() != null){
                        board.addPiece(move.getEndPosition(), new ChessPiece(teamTurn, move.getPromotionPiece()));
                    }
                    else{
                        board.addPiece(move.getEndPosition(), movingPiece);
                    }

                    movingPiece.setFirstMove(false);


                    if (movingPiece.getPieceType() == ChessPiece.PieceType.KING) {

                        board.disableCastleKingside(teamTurn);
                        board.disableCastleQueenside(teamTurn);

                    }
                    else if (movingPiece.getPieceType() == ChessPiece.PieceType.ROOK) {

                        ChessPosition start = move.getStartPosition();
                        if (start.getColumn() == 1) {

                            board.disableCastleQueenside(teamTurn);
                        }
                        else if (start.getColumn() == 8) {

                            board.disableCastleKingside(teamTurn);
                        }
                    }

                    //For Castling
                    boolean isCastle =
                            movingPiece.getPieceType() == ChessPiece.PieceType.KING
                                    && Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2;

                    if (isCastle) {
                        int row = move.getStartPosition().getRow();
                        boolean kingside = move.getEndPosition().getColumn() == 7;

                        int rookStartCol = kingside ? 8 : 1;
                        int rookEndCol = kingside ? 6 : 4;

                        ChessPosition rookStart = new ChessPosition(row, rookStartCol);
                        ChessPosition rookEnd = new ChessPosition(row, rookEndCol);

                        ChessPiece rook = board.getPiece(rookStart);
                        board.addPiece(rookEnd, rook);
                        board.addPiece(rookStart, null);
                    }

                    //For En Passant
                    if (isEnPassantCapture) {
                        ChessPosition capturedPawnPosition =
                                new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                        board.addPiece(capturedPawnPosition, null);
                    }

                    //Sets point of origin to null
                    board.addPiece(move.getStartPosition(), null);

                    board.setLastMoveMade(move);

                    teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
                } else {
                    throw new InvalidMoveException("Move not held in validMoves");
                }
            }
            else {
                throw new InvalidMoveException("Move not held in validMoves");
            }
        }
        else {
            throw new InvalidMoveException("Move not held in validMoves");
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

    //We cant use findKing because you have to look at the moves of all pieces to see if one of them has a move that can break check.
    public boolean isInCheckmate(TeamColor teamColor) {

        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= board.squares.length; row++) {
            for (int col = 1; col <= board.squares.length; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        boolean anyOptions = false;

        for (int row = 1; row <= board.squares.length; row++) {
            for (int col = 1; col <= board.squares.length; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) {
                        anyOptions = true;
                    }
                }
            }
        }
        return !anyOptions && !isInCheck(teamColor);

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

                ChessPosition targetPosition = new ChessPosition(row, col);

                ChessPiece targetPiece = board.getPiece(targetPosition);

                if ((targetPiece != null) && (targetPiece.getTeamColor() == teamColor) && (targetPiece.getPieceType() == ChessPiece.PieceType.KING)) {
                    kingPosition = new ChessPosition(row,col);
                }
            }
        }
        return kingPosition;
    }
}
