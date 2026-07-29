package client;

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
        if(tokens.length != 3) {
            return "Expected: login <USERNAME> <PASSWORD>";
        }

        try {
            var authData = serverFacade.login(tokens[1], tokens[2]);
            chessClient.signIn(authData.username(), authData.authToken());
            return "Logged in as " + authData.username();
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private String register(String[] tokens) {
        if(tokens.length != 4){
            return "register <USERNAME> <PASSWORD> <EMAIL>";
        }

        try {
            var authData = serverFacade.register(tokens[1], tokens[2], tokens[3]);
            chessClient.signIn(authData.username(), authData.authToken());
            return "User registered"+ "\nLogged in as " + authData.username();
        } catch(Exception exception) {
            return exception.getMessage();
        }
    }
}
