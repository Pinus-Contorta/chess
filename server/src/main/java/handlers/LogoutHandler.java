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

        try{
            userService.logout(authToken);
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        } catch (DataAccessException exception) {
            context.status(401);
            context.contentType("application/json");
            context.result(gson.toJson(new ErrorResponse(exception.getMessage())));
        }
    }
    private record  ErrorResponse(String message) {}
}
