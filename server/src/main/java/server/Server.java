package server;

import dataaccess.*;
import handlers.ClearApplicationHandler;
import handlers.LoginHandler;
import handlers.LogoutHandler;
import handlers.RegisterHandler;
import io.javalin.*;
import service.ClearService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO,authDAO,gameDAO);


        // Register your endpoints and exception handlers here.
        RegisterHandler registerHandler =  new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ClearApplicationHandler clearApplicationHandler= new ClearApplicationHandler(clearService);


        javalin.delete("/db", clearApplicationHandler::handle);
        javalin.delete("/session", logoutHandler::handle);

        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
