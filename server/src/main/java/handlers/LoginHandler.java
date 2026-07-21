package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class LoginHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) {
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);

        try {
            LoginResult result = userService.login(request);
            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(result));
        } catch (DataAccessException exception) {
            String message = exception.getMessage();
            int status = message.contains("invalid") ? 401
                    : message.contains("bad request") ? 400
                      : 500;
            String responseMessage = message.toLowerCase().contains("error") ? message : "Error: " + message;
            context.status(status);
            context.contentType("application/json");
            context.result(gson.toJson(new ErrorResponse(responseMessage)));
        }
    }

    private record ErrorResponse(String message) {}
}