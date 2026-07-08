package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ChessMoveCalculator {

    //Move Directions for each piece except pawns, which are screwy and are handled in the getValidMoves switch case

    private static final int[][] KING_DIR = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private static final int[][] QUEEN_DIR = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private static final int[][] BISHOP_DIR = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private static final int[][] KNIGHT_DIR = {{1, 2}, {2, 1}, {-1, 2}, {-2, 1}, {-1, -2}, {-2, -1}, {1, -2}, {2, -1}};
    private static final int[][] ROOK_DIR = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};


    private final ChessPiece piece;

    public ChessMoveCalculator(ChessPiece piece) {

        this.piece = piece;
    }


    // This method takes the start position and the piece being moved and generates a list of all legal moves, barring those which would put the king piece in danger

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition startPosition, ChessPiece piece) {

        List<ChessMove> validMoves = new ArrayList<>();

        switch (piece.getPieceType()) {
            case KING:
                for (int[] dir : KING_DIR) {
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    ChessPosition target = new ChessPosition(row, col);
                    if (board.inBounds(row, col)) {
                        isValidMove(board, startPosition, piece, validMoves, target);
                    }

                }

                // Castling

                if (piece.isFirstMove() && !isSquareUnderAttack(board, startPosition, piece.getTeamColor())) {

                    int row = startPosition.getRow();

                    tryAddCastleMove(board, startPosition, piece, validMoves, row, 8, 6, 7);
                    tryAddCastleMove(board, startPosition, piece, validMoves, row, 1, 2, 3);
                }
                return validMoves;


            case QUEEN:
                for (int[] dir : QUEEN_DIR) {
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    while (board.inBounds(row, col)) {
                        ChessPosition target = new ChessPosition(row, col);

                        if (isValidMove(board, startPosition, piece, validMoves, target)) {
                            break;
                        }

                        row += dir[0];
                        col += dir[1];
                    }
                }
                return validMoves;

            case BISHOP:
                for (int[] dir : BISHOP_DIR) {
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    while (board.inBounds(row, col)) {
                        ChessPosition target = new ChessPosition(row, col);

                        if (isValidMove(board, startPosition, piece, validMoves, target)) {
                            break;
                        }

                        row += dir[0];
                        col += dir[1];
                    }
                }
                return validMoves;

            case KNIGHT:
                for (int[] dir : KNIGHT_DIR) {
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    ChessPosition target = new ChessPosition(row, col);
                    if (board.inBounds(row, col)) {
                        isValidMove(board, startPosition, piece, validMoves, target);
                    }

                }
                return validMoves;

            case ROOK:
                for (int[] dir : ROOK_DIR) {
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    while (board.inBounds(row, col)) {
                        ChessPosition target = new ChessPosition(row, col);

                        if (isValidMove(board, startPosition, piece, validMoves, target)) {
                            break;
                        }

                        row += dir[0];
                        col += dir[1];
                    }
                }
                return validMoves;

            case PAWN:
                int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
                int promoRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

                int row = startPosition.getRow();
                int col = startPosition.getColumn();

                // 1. Forward one — EMPTY square only
                ChessPosition oneStep = new ChessPosition(row + direction, col);
                if (board.inBounds(oneStep.getRow(), oneStep.getColumn())
                        && board.getPiece(oneStep) == null) {

                    if (oneStep.getRow() == promoRow) {
                        validMoves.add(new ChessMove(startPosition, oneStep, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition, oneStep, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition, oneStep, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(startPosition, oneStep, ChessPiece.PieceType.KNIGHT));
                    } else {
                        validMoves.add(new ChessMove(startPosition, oneStep, null));
                    }

                    // 2. Forward two — only from start row, and only if one-step was also clear
                    if (row == startRow) {
                        ChessPosition twoStep = new ChessPosition(row + 2 * direction, col);
                        if (board.getPiece(twoStep) == null) {
                            validMoves.add(new ChessMove(startPosition, twoStep, null));
                        }
                    }
                }

                // 3. Diagonal captures — ENEMY only
                for (int dCol : new int[]{-1, 1}) {
                    ChessPosition diag = new ChessPosition(row + direction, col + dCol);
                    if (board.inBounds(diag.getRow(), diag.getColumn())) {
                        ChessPiece occupant = board.getPiece(diag);
                        if (occupant != null && occupant.getTeamColor() != piece.getTeamColor()) {

                            if (diag.getRow() == promoRow) {
                                validMoves.add(new ChessMove(startPosition, diag, ChessPiece.PieceType.QUEEN));
                                validMoves.add(new ChessMove(startPosition, diag, ChessPiece.PieceType.ROOK));
                                validMoves.add(new ChessMove(startPosition, diag, ChessPiece.PieceType.BISHOP));
                                validMoves.add(new ChessMove(startPosition, diag, ChessPiece.PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(startPosition, diag, null));
                            }
                        }
                    }
                }

                // 4. En passant
                ChessMove lastMove = board.getLastMoveMade();

                if (lastMove != null) {
                    ChessPosition lastEnd = lastMove.getEndPosition();
                    ChessPosition lastStart = lastMove.getStartPosition();
                    ChessPiece lastMovedPiece = board.getPiece(lastEnd);

                    boolean wasTwoSquarePawnMove =
                            lastMovedPiece != null
                                    && lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN
                                    && Math.abs(lastEnd.getRow() - lastStart.getRow()) == 2;

                    boolean isAdjacent =
                            wasTwoSquarePawnMove
                                    && lastEnd.getRow() == row
                                    && Math.abs(lastEnd.getColumn() - col) == 1;

                    if (isAdjacent) {
                        ChessPosition enPassantEnd = new ChessPosition(row + direction, lastEnd.getColumn());
                        ChessMove enPassantMove = new ChessMove(startPosition, enPassantEnd, null);
                        enPassantMove.setEnPassant(true);
                        validMoves.add(enPassantMove);
                    }
                }

                return validMoves;
        }


        return null;
    }

    private boolean isValidMove(ChessBoard board, ChessPosition startPosition, ChessPiece piece, List<ChessMove> validMoves, ChessPosition target) {
        //First checks to see if the target is in bounds
        if (board.inBounds(target.getRow(), target.getColumn())) {
            if (board.getPiece(target) == null) {
                validMoves.add(new ChessMove(startPosition, target, null));
            } else {
                //Checks if piece in movepath is a capturable enemy. If it is, then that square is added to validMoves.
                if (board.getPiece(target).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(startPosition, target, null));
                }
                return true;
            }
            return false;

        } else {
            return false;
        }
    }


    //Helper methods for Castling

    private void tryAddCastleMove(ChessBoard board, ChessPosition kingStart, ChessPiece king, List<ChessMove> validMoves, int row, int rookCol, int kingDestCol, int passThroughCol) {

        ChessPosition rookPos = new ChessPosition(row, rookCol);
        ChessPiece rook = board.getPiece(rookPos);

        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.getTeamColor() != king.getTeamColor() || !rook.isFirstMove()) {
            return;
        }

        // Check all squares between king and rook are empty
        int step = (rookCol > kingStart.getColumn()) ? 1 : -1;

        for (int col = kingStart.getColumn() + step; col != rookCol; col += step) {

            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return;
            }

        }

        // King must not pass through or land on an attacked square
        if (isSquareUnderAttack(board, new ChessPosition(row, passThroughCol), king.getTeamColor()) || isSquareUnderAttack(board, new ChessPosition(row, kingDestCol), king.getTeamColor())) {
            return;
        }

        validMoves.add(new ChessMove(kingStart, new ChessPosition(row, kingDestCol), null));
    }

    private boolean isSquareUnderAttack(ChessBoard board, ChessPosition square, ChessGame.TeamColor teamColor) {
        for (int row = 1; row <= board.squares.length; row++) {

            for (int col = 1; col <= board.squares.length; col++) {


                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece enemy = board.getPiece(pos);

                if (enemy != null && enemy.getTeamColor() != teamColor) {


                    if (enemy.getPieceType() == ChessPiece.PieceType.KING) {


                        // Handle king threats directly — never recurse into castling logic
                        int rowDiff = Math.abs(pos.getRow() - square.getRow());
                        int colDiff = Math.abs(pos.getColumn() - square.getColumn());

                        if (rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0)) {
                            return true;
                        }
                    } else {

                        Collection<ChessMove> enemyMoves = new ChessMoveCalculator(enemy).getValidMoves(board, pos, enemy);

                        for (ChessMove m : enemyMoves) {


                            if (m.getEndPosition().equals(square)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
