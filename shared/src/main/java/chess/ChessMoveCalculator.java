package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ChessMoveCalculator {

    //Move Directions for each piece except pawns, which are screwy and are handled in the getValidMoves switch case

    private static final int[][] KING_DIR = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
    private static final int[][] QUEEN_DIR = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
    private static final int[][] BISHOP_DIR = {{-1,-1},{-1,1},{1,-1},{1,1}};
    private static final int[][] KNIGHT_DIR = {{1,2},{2,1},{-1,2},{-2,1},{-1,-2},{-2,-1},{1,-2},{2,-1}};
    private static final int[][] ROOK_DIR = {{1,0},{0,1},{-1,0},{0,-1}};


    private final ChessPiece piece;

    public ChessMoveCalculator(ChessPiece piece){

        this.piece = piece;
    }


    // This method takes the start position and the piece being moved and generates a list of all legal moves, barring those which would put the king piece in danger
    //TODO: In the middle of implimenting the PAWN. Needs to handle promotion.
    public Collection<ChessMove> getValidMoves(ChessBoard board,ChessPosition startPosition, ChessPiece piece) {

        List<ChessMove> validMoves = new ArrayList<>();

        switch (piece.getPieceType()) {
            case KING:
                for(int[] dir : KING_DIR){
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    ChessPosition target = new ChessPosition(row, col);
                    if (board.inBounds(row,col)) {
                        isValidMove(board, startPosition, piece, validMoves, target);
                    }

                }
                return validMoves;


            case QUEEN:
                for (int[] dir : QUEEN_DIR){
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
                for (int[] dir : BISHOP_DIR){
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
                for(int[] dir : KNIGHT_DIR){
                    int row = startPosition.getRow() + dir[0];
                    int col = startPosition.getColumn() + dir[1];

                    ChessPosition target = new ChessPosition(row, col);
                    if (board.inBounds(row,col)) {
                        isValidMove(board, startPosition, piece, validMoves, target);
                    }

                }
                return validMoves;

            case ROOK:
                for (int[] dir : ROOK_DIR){
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
                return List.of(new ChessMove(startPosition, new ChessPosition(5,5), null));

        }


        return validMoves;
    }

    private boolean isValidMove(ChessBoard board, ChessPosition startPosition, ChessPiece piece, List<ChessMove> validMoves, ChessPosition target) {
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
    }
}
