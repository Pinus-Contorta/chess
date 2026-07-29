package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

public class BoardPrinter {
    public BoardPrinter() {
    }

    public static String printBoard(boolean isWhiteView) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        int[] rows = isWhiteView ? decending(8,1) : ascending (1,8);
        int[] columns = isWhiteView ? ascending (1,8) : decending(8,1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(columnLabels(columns));

        for(int row : rows) {
            stringBuilder.append(rowLabel(row));
            for(int col : columns) {
                stringBuilder.append(square(board, row, col));
            }
            stringBuilder.append(rowLabel(row)).append("\n");
        }

    }

    private static int[] ascending(int from, int to) {
        int[] result = new int[to - from + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = from + i;
        }
        return result;
    }

    private static int[] decending(int from, int to) {
        int[] result = new int[to - from + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = from - i;
        }
        return result;
    }


    //Label functions
    private static String columnLabels(int[] columns) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int col : columns) {
            char letter = (char) ('a' + col - 1);
            stringBuilder.append(" ").append(letter).append(" ");
        }

        return stringBuilder.append("\n").toString();
    }

    private static String rowLabel(int row) {
        return " " + row + " ";
    }
    //Label functions

    private static String square(ChessBoard board, int row, int col) {
        boolean isLight = (row + col) % 2 != 0;

        String bg = isLight ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;

        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        String glyph = pieceGlyph(piece);
        String stringColor = piece == null ? "" :
                (piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                                                   EscapeSequences.SET_TEXT_COLOR_BLACK :
                                                   EscapeSequences.SET_TEXT_COLOR_WHITE);

        return bg + stringColor + glyph + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
    }

    private static String pieceGlyph(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }



}
