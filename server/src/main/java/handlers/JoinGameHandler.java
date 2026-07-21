package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.JoinGameRequest;

public class JoinGameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context context) {
        String authToken = context.header("authorization");
        JoinGameRequest request = gson.fromJson(context.body(), JoinGameRequest.class);

        try {
            gameService.joinGame(authToken, request);
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        } catch (DataAccessException exception) {
            String message = exception.getMessage();
            int status = message.contains("unauthorized") ? 401
                    : message.contains("already taken") ? 403
                      : message.contains("bad request") ? 400
                        : 500;
            context.status(status);
            context.contentType("application/json");
            String responseMessage = message.toLowerCase().contains("error") ? message : "Error: " + message;
            context.result(gson.toJson(new ErrorResponse(responseMessage)));
        }
    }

    private record ErrorResponse(String message) {}
}