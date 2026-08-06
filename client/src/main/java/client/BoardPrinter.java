package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.Set;

public class BoardPrinter {

    public static String printBoard(ChessBoard board, boolean isWhiteView) {
        return printBoard(board, isWhiteView, Set.of());
    }

    public static String printBoard(ChessBoard board, boolean isWhiteView, Set<ChessPosition> highlights) {
        int[] rows = isWhiteView ? descending(8, 1) : ascending(1, 8);
        int[] columns = isWhiteView ? ascending(1, 8) : descending(8, 1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(columnLabels(columns));

        for (int row : rows) {
            stringBuilder.append(rowLabel(row));
            for (int col : columns) {
                stringBuilder.append(square(board, row, col, highlights));
            }
            stringBuilder.append(rowLabel(row)).append("\n");
        }
        stringBuilder.append(columnLabels(columns));
        return stringBuilder.toString();
    }

    private static int[] ascending(int from, int to) {
        int[] result = new int[to - from + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = from + i;
        }
        return result;
    }

    private static int[] descending(int from, int to) {
        int[] result = new int[from - to + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = from - i;
        }
        return result;
    }

    //Label functions
    private static String columnLabels(int[] columns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(EscapeSequences.EMPTY);
        for (int col : columns) {
            char letter = (char) ('a' + col - 1);
            stringBuilder.append(letter).append(EscapeSequences.WIDE_PAD).append(" ");
        }
        stringBuilder.append(EscapeSequences.EMPTY); // trailing corner
        return stringBuilder.append("\n").toString();
    }

    private static String rowLabel(int row) {
        return " " + row + EscapeSequences.WIDE_PAD;
    }
    //Label functions

    private static String square(ChessBoard board, int row, int col, Set<ChessPosition> highlights) {
        ChessPosition position = new ChessPosition(row, col);
        boolean isLight = (row + col) % 2 != 0;
        boolean highlighted = highlights != null && highlights.contains(position);

        String bg = highlighted
                ? (isLight ? EscapeSequences.SET_BG_COLOR_YELLOW : EscapeSequences.SET_BG_COLOR_DARK_GREEN)
                : (isLight ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK);

        ChessPiece piece = board.getPiece(position);
        String glyph = pieceGlyph(piece);
        String stringColor = piece == null ? "" :
                (piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                 EscapeSequences.SET_TEXT_COLOR_RED :
                 EscapeSequences.SET_TEXT_COLOR_BLUE);

        //Reset calls prevent color bleed.
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
