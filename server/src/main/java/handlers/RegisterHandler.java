package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class RegisterHandler {

    private final UserService userService;
    private final Gson gson = new Gson();


    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) {
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);

        try{
            RegisterResult result = userService.register(request);
            context.status(200);
            context.json(result);
        }catch (DataAccessException exception){
            String message = exception.getMessage();

            int status;

            //I can see this being finnicky, there might be a better way to pull this off than to hard code it.
            if (message.contains("bad request")) {
                status = 400;
            } else if (message.contains("already taken")) {
                status = 403;
            }else {
                status = 500;
            }
            context.status(status);
            context.json(new ErrorResponse(message));
        }

    }

    private record  ErrorResponse(String message) {}
}
