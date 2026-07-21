package server;

import dataaccess.*;
import handlers.*;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Unable to initialize database", ex);
        }

        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO,authDAO,gameDAO);
        GameService gameService = new GameService(gameDAO,authDAO);


        // Register your endpoints and exception handlers here.
        RegisterHandler registerHandler =  new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ClearApplicationHandler clearApplicationHandler = new ClearApplicationHandler(clearService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);

        //DELETE
        javalin.delete("/db", clearApplicationHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        //POST
        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.post("/game", createGameHandler::handle);
        //GET
        javalin.get("/game", listGamesHandler::handle);
        //PUT
        javalin.put("/game", joinGameHandler::handle);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
