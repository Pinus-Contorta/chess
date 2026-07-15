package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearApplicationHandler {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearApplicationHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void handle(Context context) {
        try {
            clearService.clear();
            context.status(200);
            context.contentType("application/json");
            context.result("{}");
        } catch (DataAccessException exception) {
            context.status(500);
            context.contentType("application/json");
            context.result(gson.toJson(new ErrorResponse(exception.getMessage())));
        }
    }
    private record ErrorResponse(String message) {}
}
