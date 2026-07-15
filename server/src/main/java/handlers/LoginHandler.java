package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class LoginHandler {
    private final UserService userService;
    private final Gson gson = new Gson();


    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle (Context context) {
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);

        try{
            LoginResult result = userService.login(request);
            context.status(200);
            context.contentType("application/json");
            context.result(gson.toJson(result));
        }catch (DataAccessException exception) {
            String message = exception.getMessage();

            int status = message.contains("invalid") ? 401
                    : message.contains("bad request") ? 400
                    : 500;
            context.status(status);
            context.contentType("application/json");
            context.result(gson.toJson(new ErrorResponse(message)));
        }
    }

    private record ErrorResponse(String message) {}
}
