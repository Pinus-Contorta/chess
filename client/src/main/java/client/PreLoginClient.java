package client;

import java.util.Locale;

public class PreLoginClient {

    private final ServerFacade serverFacade;
    private final ChessClient chessClient;
    public PreLoginClient(ServerFacade serverFacade, ChessClient chessClient) {
        this.serverFacade = serverFacade;
        this.chessClient = chessClient;
    }

    public String eval(String inputString){

        var tokens = inputString.trim().split("\\s+");
        var command = tokens.length > 0 ? tokens[0].toLowerCase() : "help";

        return switch (command) {
            case "quit" -> "quit";
            case "login" -> login(tokens);
            case "register" -> register(tokens);
            default -> help();
        };
    }

    private String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create a new account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    private String login(String[] tokens) {
        //TODO:Implement PreLoginClient.login
        return"";
    }

    private String register(String[] tokens) {
        //TODO:Implement PreLoginClient.register
        return"";
    }
}
