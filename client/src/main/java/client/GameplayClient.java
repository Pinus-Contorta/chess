package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;

public class GameplayClient implements ServerMessageObserver {

    private final ChessClient chessClient;
    private final int gameID;
    private final String playerColor; // "WHITE", "BLACK", or null if observing

    private final WebSocketFacade webSocketFacade;
    private volatile GameData currentGame;
    private boolean resignPending = false;

    public GameplayClient(ChessClient chessClient, int port, int gameID, String playerColor) throws Exception {
        this.chessClient = chessClient;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.webSocketFacade = new WebSocketFacade(port, this);
    }

    public String connect() throws Exception {
        webSocketFacade.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, chessClient.getAuthToken(), gameID));
        return "Connecting to game " + gameID + "...";
    }

    /**
     * Closes the socket without sending LEAVE. Used when the initial CONNECT never
     * succeeded, so there's nothing registered server-side to notify — this just
     * makes sure we don't leak the connection.
     */
    void abort() {
        webSocketFacade.close();
    }

    public String eval(String inputString) {
        var tokens = inputString.trim().split("\\s+");
        var command = tokens.length > 0 ? tokens[0].toLowerCase() : "help";

        return switch (command) {
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> move(tokens);
            case "resign" -> resign(tokens);
            case "highlight" -> highlight(tokens);
            default -> help();
        };
    }

    private String help() {
        return """
                help - with possible commands
                redraw - the chess board
                leave - the game
                move <FROM> <TO> [PROMOTION] - e.g. move e2 e4, move e7 e8 q
                resign - forfeit the game (asks for confirmation)
                highlight <SQUARE> - show legal moves for the piece there, e.g. highlight e2
                """;
    }

    private String redraw() {
        if (currentGame == null) {
            return "Board not loaded yet.";
        }
        return BoardPrinter.printBoard(currentGame.game().getBoard(), isWhiteView());
    }

    private String leave() {
        try {
            webSocketFacade.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, chessClient.getAuthToken(), gameID));
        } catch (Exception ignored) {
            // best effort — we're leaving locally regardless
        }
        webSocketFacade.close();
        chessClient.leaveGameplay();
        return "Left the game.";
    }

    private String move(String[] tokens) {
        if (playerColor == null) {
            return "Error: observers cannot make moves.";
        }
        if (currentGame != null && currentGame.game().isGameOver()) {
            return "Error: the game is over. No more moves can be made.";
        }
        if (tokens.length != 3 && tokens.length != 4) {
            return "Expected: move <FROM> <TO> [PROMOTION]  e.g. move e2 e4";
        }

        ChessPosition start;
        ChessPosition end;
        try {
            start = parseSquare(tokens[1]);
            end = parseSquare(tokens[2]);
        } catch (Exception exception) {
            return "Error: invalid square. Use letter+number, e.g. e2";
        }

        ChessPiece.PieceType promotion = null;
        if (tokens.length == 4) {
            promotion = parsePromotion(tokens[3]);
            if (promotion == null) {
                return "Error: invalid promotion piece. Use one of Q, R, B, N";
            }
        }

        try {
            webSocketFacade.send(new MakeMoveCommand(chessClient.getAuthToken(), gameID, new ChessMove(start, end, promotion)));
            return "Move sent.";
        } catch (Exception exception) {
            return "Error: unable to send move.";
        }
    }

    private String resign(String[] tokens) {
        if (tokens.length == 2 && (tokens[1].equalsIgnoreCase("confirm") || tokens[1].equalsIgnoreCase("yes"))) {
            if (!resignPending) {
                return "Type 'resign' first to begin resigning.";
            }
            resignPending = false;
            try {
                webSocketFacade.send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, chessClient.getAuthToken(), gameID));
                return "Resignation sent.";
            } catch (Exception exception) {
                return "Error: unable to send resignation.";
            }
        }

        resignPending = true;
        return "Are you sure you want to resign? Type 'resign confirm' to forfeit the game.";
    }

    private String highlight(String[] tokens) {
        if (currentGame == null) {
            return "Board not loaded yet.";
        }
        if (tokens.length != 2) {
            return "Expected: highlight <SQUARE>  e.g. highlight e2";
        }

        ChessPosition position;
        try {
            position = parseSquare(tokens[1]);
        } catch (Exception exception) {
            return "Error: invalid square. Use letter+number, e.g. e2";
        }

        ChessGame game = currentGame.game();
        var moves = game.validMoves(position);

        Set<ChessPosition> highlights = new HashSet<>();
        highlights.add(position);
        if (moves != null) {
            for (ChessMove move : moves) {
                highlights.add(move.getEndPosition());
            }
        }

        return BoardPrinter.printBoard(game.getBoard(), isWhiteView(), highlights);
    }

    private boolean isWhiteView() {
        return playerColor == null || playerColor.equals("WHITE");
    }

    private ChessPosition parseSquare(String square) {
        if (square.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + square);
        }
        char file = Character.toLowerCase(square.charAt(0));
        char rank = square.charAt(1);
        int col = file - 'a' + 1;
        int row = rank - '0';
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid square: " + square);
        }
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotion(String token) {
        return switch (token.toUpperCase()) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "N" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                currentGame = ((LoadGameMessage) message).getGame();
                resignPending = false;
                System.out.println("\n" + BoardPrinter.printBoard(currentGame.game().getBoard(), isWhiteView()));
            }
            case NOTIFICATION -> System.out.println("\n" + ((NotificationMessage) message).getMessage());
            case ERROR -> System.out.println("\n" + ((ErrorMessage) message).getErrorMessage());
        }
    }
}