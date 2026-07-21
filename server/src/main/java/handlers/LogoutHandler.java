package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;

public class LogoutHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) {
        String authToken = context.header("authorization");

        try {
            userService.logout(authToken);
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        } catch (DataAccessException exception) {
            String message = exception.getMessage();
            int status = message.toLowerCase().contains("unauthorized") ? 401 : 500;
            String responseMessage = message.toLowerCase().contains("error") ? message : "Error: " + message;
            context.status(status);
            context.contentType("application/json");
            context.result(gson.toJson(new ErrorResponse(responseMessage)));
        }
    }
    private record  ErrorResponse(String message) {}
}
