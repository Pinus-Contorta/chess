package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import service.GameService;
import service.MoveResult;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {

    private final GameService gameService;
    private final AuthDAO authDAO;

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    //TODO: consider moving this in with the rest of the handlers for th server.

    public WebSocketHandler(GameService gameService, AuthDAO authDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
    }

    public void configure(WsConfig wsConfig) {
        wsConfig.onMessage(this::onMessage);
        wsConfig.onClose(this::onClose);
    }

    private void onMessage(WsMessageContext context) {
        UserGameCommand command = gson.fromJson(context.message(), UserGameCommand.class);

        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(context,command);
                case MAKE_MOVE -> makeMove(context, gson.fromJson(context.message(), MakeMoveCommand.class));
                case LEAVE -> leave(context, command);
                case RESIGN -> resign(context, command);
            }
        } catch (DataAccessException exception) {
            connections.sendToRoot(context, new ErrorMessage(exception.getMessage()));
        } catch (Exception exception) {
            connections.sendToRoot(context, new ErrorMessage("Error: " + exception.getMessage()));
        }
    }

    private void connect(WsMessageContext context, UserGameCommand command) throws DataAccessException {
        AuthData authentication = validateAuth(command.getAuthToken());
        GameData game = gameService.connect(command.getAuthToken(), command.getGameID());

        connections.add(command.getGameID(), authentication.username(), context);
        connections.sendToRoot(context, new LoadGameMessage(game));

        String role = roleDescription(game, authentication.username());
        connections.broadcast(command.getGameID(), authentication.username(),
                new NotificationMessage(authentication.username() + " connected " + role));
    }

    private void makeMove(WsMessageContext context, MakeMoveCommand command) throws DataAccessException {
        AuthData authentication = validateAuth(command.getAuthToken());
        MoveResult result = gameService.makeMove(command.getAuthToken(), command.getGameID(), command.getMove());

        connections.broadcast(command.getGameID(), null, new LoadGameMessage(result.game()));
        connections.broadcast(command.getGameID(), authentication.username(),
                new NotificationMessage(authentication.username() + " moved " + describeMove(command.getMove())));

        ChessGame chessGame = result.game().game();
        ChessGame.TeamColor opponent = result.moverColor() == ChessGame.TeamColor.WHITE
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String opponentName = opponent == ChessGame.TeamColor.WHITE
                ? result.game().whiteUsername() : result.game().blackUsername();

        if (chessGame.isInCheckmate(opponent)) {
            connections.broadcast(command.getGameID(), null, new NotificationMessage(opponentName + " is in checkmate"));
        } else if (chessGame.isInStalemate(opponent)) {
            connections.broadcast(command.getGameID(), null, new NotificationMessage("Game is in stalemate"));
        } else if (chessGame.isInCheck(opponent)) {
            connections.broadcast(command.getGameID(), null, new NotificationMessage(opponentName + " is in check"));
        }
    }

    private void leave(WsMessageContext context, UserGameCommand command) throws DataAccessException {
        AuthData authentication = validateAuth(command.getAuthToken());
        gameService.leave(command.getAuthToken(), command.getGameID());

        connections.remove(context);
        connections.broadcast(command.getGameID(), authentication.username(),
                new NotificationMessage(authentication.username() + " left the game"));
    }

    private void resign(WsMessageContext context, UserGameCommand command) throws DataAccessException {
        AuthData auth = validateAuth(command.getAuthToken());
        gameService.resign(command.getAuthToken(), command.getGameID());

        connections.broadcast(command.getGameID(), null, new NotificationMessage(auth.username() + " resigned"));
    }

    private void onClose(WsCloseContext ctx) {
        connections.remove(ctx);
    }

    private AuthData validateAuth(String authToken) throws DataAccessException {
        if(authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return authDAO.getAuth(authToken);
    }

    private String roleDescription(GameData game, String username) {
        return username.equals(game.whiteUsername()) ? "as WHITE" :
                username.equals(game.blackUsername()) ? "as BLACK" : "as an observer";
    }

    private String describeMove(ChessMove move) {
        return move.getStartPosition() + " to " + move.getEndPosition();
    }
}
