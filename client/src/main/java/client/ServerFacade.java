package client;

import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {

    private final String coreURL;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    public ServerFacade(int port) {
        this.coreURL = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", null, request, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", null, request, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", authToken, null, null);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        var request = new CreateGameRequest(gameName);
        var response = makeRequest("POST", "/game", authToken, request, CreateGameResponse.class);
        return response.gameID();
    }

    public List<GameSummary> listGames(String authToken) throws ResponseException {
        var response = makeRequest("GET", "/game", authToken, null, ListGamesResponse.class);
        return response.games();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        var request = new JoinGameRequest(playerColor, gameID);
        makeRequest("PUT", "/game", authToken, request, null);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest (String method, String path, String authToken,
                               Object requestBody, Class<T> responseClass) throws ResponseException {

        try {
            var uri = URI.create(coreURL + path);
            var builder = HttpRequest.newBuilder(uri);

            HttpRequest.BodyPublisher bodyPublisher;
            if (requestBody != null) {
                builder.header("Content-Type", "application/json");
                bodyPublisher = HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody));
            } else {
                bodyPublisher = HttpRequest.BodyPublishers.noBody();
            }
            builder.method(method, bodyPublisher);

            if (authToken != null) {
                builder.header("Authorization", authToken);
            }

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() / 100 != 2) {
                String message = "Error: server returned status " + response.statusCode();
                try {
                    var error = gson.fromJson(response.body(), ErrorResponse.class);
                    if (error != null && error.message() != null) {
                        message = error.message();
                    }
                } catch (Exception ignored) {
                    // response body wasn't the expected JSON shape; fall back to the default message
                }
                throw new ResponseException(response.statusCode(), message);
            }

            if (responseClass == null) {
                return null;
            }
            return gson.fromJson(response.body(), responseClass);

        } catch (IOException | InterruptedException exception) {
            throw new ResponseException(500, "Error: unable to connect to the server");
        }

    }

    private record RegisterRequest(String username, String password, String email) {}
    private record LoginRequest(String username, String password) {}
    private record CreateGameRequest(String gameName) {}
    private record CreateGameResponse(int gameID) {}
    private record JoinGameRequest(String playerColor, int gameID) {}
    private record ListGamesResponse(List<GameSummary> games) {}
    private record ErrorResponse(String message) {}
}
