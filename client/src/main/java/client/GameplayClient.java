package client;

import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

public class GameplayClient implements ServerMessageObserver {

    private final ChessClient chessClient;
    private final int gameID;
    private final String playerColor;

    private final WebSocketFacade webSocketFacade;


    public GameplayClient(ChessClient chessClient, int port, int gameID, String playerColor) throws Exception {
    }

    @Override
    public void notify(ServerMessage message) {

    }
}
