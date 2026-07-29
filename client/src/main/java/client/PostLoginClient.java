package client;

import java.util.List;

public class PostLoginClient {

    private final ServerFacade serverFacade;
    private final ChessClient chessClient;

    private List<GameSummary> lastListedGames = List.of();

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
        if(tokens.length != 2) {
            return "Expected: create <NAME>";
        }

        if(chessClient.getAuthToken() == null) {
            return "Error: Bad authentication token";
        }

        try{
            serverFacade.createGame(chessClient.getAuthToken(), tokens[1]);
            return "Created game: " + tokens[1];
        }catch (Exception exception) {
            return "Error: " + exception.getMessage();
        }
    }

    private String listGames() {
        try {
            lastListedGames = serverFacade.listGames(chessClient.getAuthToken());

            if (lastListedGames.isEmpty()) {
                return "No games exist yet. Use 'create' to make one.";
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lastListedGames.size(); i++) {
                GameSummary game = lastListedGames.get(i);
                String white = game.whiteUsername() != null ? game.whiteUsername() : "empty";
                String black = game.blackUsername() != null ? game.blackUsername() : "empty";
                result.append(i + 1).append(". ").append(game.gameName())
                        .append(" (White: ").append(white)
                        .append(", Black: ").append(black).append(")\n");
            }
            return result.toString();

        } catch (ResponseException exception) {
            return "Error: " + exception.getMessage();
        }
    }

    private String joinGame(String[] tokens) {
        if (tokens.length != 3) {

            return "Expected: join <ID> [WHITE|BLACK]";
        }

        int gameNumber;

        try{
            gameNumber = Integer.parseInt(tokens[1]);
        }catch (Exception exception) {
            return "Expected a number, got: " + tokens[1];
        }

        int gameIndex = gameNumber - 1;

        if(gameIndex < 0 || gameIndex >= lastListedGames.size()) {
            return "No game numbered " + gameNumber + ". Use 'list' command.";
        }

        int gameID = lastListedGames.get(gameIndex).gameID();

        String playerColor = tokens[2].toUpperCase();
        if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return "Expected WHITE or BLACK for color, got: " + tokens[2];
        }

        try {
            serverFacade.joinGame(chessClient.getAuthToken(), playerColor, gameID);
            return "Joined game " + gameNumber + " as " + playerColor + "\n" + BoardPrinter.printBoard(playerColor.equals("WHITE"));
        }catch (Exception exception){
            return "Error: " + exception.getMessage();
        }

    }


    private String observeGame(String[] tokens) {

        if(tokens.length != 2) {
            return "Expected: observe <ID>";
        }

        int gameNumber;

        try{
            gameNumber = Integer.parseInt(tokens[1]);
        }catch (Exception exception) {
            return "Expected a number, got: " + tokens[1];
        }

        int gameIndex = gameNumber = 1;

        if(gameIndex < 0 || gameIndex >= lastListedGames.size()) {
            return "No game numbered " + gameNumber + ". Use 'list' command.";
        }

        return "Observing game: " + gameNumber + "\n" + BoardPrinter.printBoard(true);

    }

    private String logout() {
        try {
            serverFacade.logout(chessClient.getAuthToken());
            chessClient.signOut();
            return "Logged out.";
        } catch (ResponseException exception) {
            return "Error: " + exception.getMessage();
        }
    }


    //I know this isn't in the spec, but I ain't gonna log out every time I wanna rage-quit a match.
    private String quit() {
        try {
            serverFacade.logout(chessClient.getAuthToken());
        } catch (ResponseException ignored) {
            // already logged out or token invalid — fine, we're quitting anyway
        }
        chessClient.signOut();
        return "quit";
    }

}