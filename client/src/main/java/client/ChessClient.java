package client;

public class ChessClient {

    private State state = State.SIGNED_OUT;
    private String authToken;
    private String username;
    private final int port;

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private GameplayClient gameplayClient;

    public ChessClient(int port) {
        this.port = port;
        ServerFacade serverFacade = new ServerFacade(port);
        preLoginClient = new PreLoginClient(serverFacade, this);
        postLoginClient = new PostLoginClient(serverFacade, this);
    }

    public String eval(String inputString) {
        try {
            return switch (state) {
                case SIGNED_OUT -> preLoginClient.eval(inputString);
                case SIGNED_IN -> postLoginClient.eval(inputString);
                case GAMEPLAY -> gameplayClient.eval(inputString);
            };
        } catch (Exception exception) {
            String message = exception.getMessage();
            return message != null ? message : "An unexpected error occurred. Please try again.";
        }
    }

    public String getPromptLabel() {
        return state.name();
    }

    String enterGameplay(int gameID, String playerColor) throws Exception {
        gameplayClient = new GameplayClient(this, port, gameID, playerColor);
        state = State.GAMEPLAY;
        return gameplayClient.connect();
    }

    void leaveGameplay() {
        gameplayClient = null;
        state = State.SIGNED_IN;
    }

    void signIn(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
        this.state = State.SIGNED_IN;
    }

    void signOut() {
        this.username = null;
        this.authToken = null;
        this.state = State.SIGNED_OUT;
    }

    String getAuthToken() {
        return authToken;
    }

    String getUsername() {
        return username;
    }
}
