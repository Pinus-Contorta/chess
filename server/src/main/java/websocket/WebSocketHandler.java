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
                case MAKE_MOVE -> makeMove(context, gson.fromJson(context.message(), UserGameCommand.class));
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

    }

    private void makeMove(WsMessageContext context, UserGameCommand command) throws DataAccessException {

    }

    private void leave(WsMessageContext context, UserGameCommand command) throws DataAccessException {

    }

    private void resign(WsMessageContext context, UserGameCommand command) throws DataAccessException {

    }

    private void onClose(WsCloseContext ctx) {
        connections.remove(ctx);
    }
}
