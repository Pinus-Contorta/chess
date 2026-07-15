package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.ListGamesResult;
import service.UserService;

public class ListGamesHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void handle(Context context) {
        String authToken = context.header("authorization");

        try{
            ListGamesResult result = gameService.listGames(authToken);
            context.status(200);
            context.contentType("application.json");
            context.result(gson.toJson(result));
        }catch(DataAccessException exception) {
            context.status(401);
            context.contentType("applicaiton/json");
            context.result(gson.toJson(new ErrorResponse(exception.getMessage())));
        }

    }
    private record ErrorResponse(String message) {}
}
