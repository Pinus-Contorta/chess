package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private final Gson gson = new Gson();
    private final ServerMessageObserver observer;
    private Session session;

    public WebSocketFacade(int port, ServerMessageObserver observer) throws Exception {
        this.observer = observer;

        URI socketURI = new URI("ws://localhost:" + port + "/ws");
        var container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String json) {
                observer.notify(readMessage(json));
            }
        });
    }

    private ServerMessage readMessage(String json) {
        ServerMessage base = gson.fromJson(json, ServerMessage.class);
        return switch (base.getServerMessageType()) {
            case LOAD_GAME -> gson.fromJson(json, LoadGameMessage.class);
            case ERROR -> gson.fromJson(json, ErrorMessage.class);
            case NOTIFICATION -> gson.fromJson(json, NotificationMessage.class);
        };
    }

    public void send(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() {
        try {
            session.close();
        } catch (Exception ignored) {}
    }
}
