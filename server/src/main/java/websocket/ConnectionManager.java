package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {

    private record Connection(String username, WsContext session) {}

    private final Map<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(int gameID, String username, WsContext session) {
        gameConnections.computeIfAbsent(gameID, id -> new CopyOnWriteArrayList<>())
                .add(new Connection(username, session));
    }

    public void remove(WsContext session) {

        String id = session.sessionId();

        for (List<Connection> connections : gameConnections.values()) {
            connections.removeIf(connection -> connection.session().sessionId().equals(id));
        }
    }

    public void sendToRoot(WsContext session, ServerMessage message) {
        session.send(gson.toJson(message));
    }

    public void broadcast(int gameID, String excludeUsername, ServerMessage message) {
        var connections = gameConnections.get(gameID);

        if(connections == null) {
            return;
        }

        String json = gson.toJson(message);

        for (Connection connection : connections) {
            if(connection.username().equals(excludeUsername)){
                continue;
            }

            try{
                connection.session().send(json);
            } catch (Exception ignored) {}
        }
    }
}
