package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.ListGamesResult;

public class ListGamesHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context context) {
        String authToken = context.header("authorization");

        try {
            ListGamesResult result = gameService.listGames(authToken);
            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(result));
        } catch (DataAccessException exception) {
            String message = exception.getMessage();
            int status = message.contains("unauthorized") ? 401 : 500;
            String responseMessage = message.toLowerCase().contains("error") ? message : "Error: " + message;
            context.status(status);
            context.contentType("application/json");
            context.result(gson.toJson(new ErrorResponse(responseMessage)));
        }
    }
    private record ErrorResponse(String message) {}
}