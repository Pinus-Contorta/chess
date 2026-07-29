package client;

public class PostLoginClient {

    private final ServerFacade serverFacade;
    private final ChessClient chessClient;

    public PostLoginClient(ServerFacade serverFacade, ChessClient chessClient) {
        this.serverFacade = serverFacade;
        this.chessClient = chessClient;
    }

    public String eval(String inputString) {

        var tokens = inputString.trim().split("\\s+");
        var command = tokens.length > 0 ? tokens[0].toLowerCase() : "help";

        return switch (command) {
            case "create" -> createGame(tokens);
            case "list" -> listGames();
            case "join" -> joinGame(tokens);
            case "observe" -> observeGame(tokens);
            case "logout" -> logout();
            case "quit" -> quit();
            default -> help();
        };
    }

    private String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    private String createGame(String[] tokens) {
        return "not yet implemented";
    }

    private String listGames() {
        return "not yet implemented";
    }

    private String joinGame(String[] tokens) {
        return "not yet implemented";
    }

    private String observeGame(String[] tokens) {
        return "not yet implemented";
    }

    private String logout() {
        return "not yet implemented";
    }

    private String quit() {
        return "not yet implemented";
    }

}