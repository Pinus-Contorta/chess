package chess;

import java.util.Collection;
import java.util.List;

public class ChessMoveCalculator {


    private final ChessPiece piece;

    public ChessMoveCalculator(ChessPiece piece){

        this.piece = piece;
    }


    // This method takes the start position and the piece being moved and generates a list of all legal moves, barring those which would put the king piece in danger
    //TODO: This method is hardcoded while I try and get the architecture working the way I want.
    public Collection<ChessMove> getValidMoves(ChessPosition startPosition, ChessPiece piece) {

        switch (piece.getPieceType()) {
            case KING:
                return List.of(new ChessMove(startPosition, new ChessPosition(0,0), null));

            case QUEEN:
                return List.of(new ChessMove(startPosition, new ChessPosition(1,1), null));

            case BISHOP:
                return List.of(new ChessMove(startPosition, new ChessPosition(2,2), null));

            case KNIGHT:
                return List.of(new ChessMove(startPosition, new ChessPosition(3,3), null));

            case ROOK:
                return List.of(new ChessMove(startPosition, new ChessPosition(4,4), null));

            case PAWN:
                return List.of(new ChessMove(startPosition, new ChessPosition(5,5), null));

        }


        return null;
    }
}
