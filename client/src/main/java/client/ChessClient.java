package client;

public class ChessClient {

    private State state = State.SIGNED_OUT;
    private String authToken;
    private String username;

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;

    public ChessClient(int port) {
        ServerFacade serverFacade = new ServerFacade(port);
        preLoginClient = new PreLoginClient(serverFacade, this);
        postLoginClient = new PostLoginClient(serverFacade, this);
    }

    public String eval(String inputString) {
        try {
            return switch (state) {
                case SIGNED_OUT -> preLoginClient.eval(inputString);
                case SIGNED_IN -> postLoginClient.eval(inputString);
            };
        } catch (Exception exception) {
            String message = exception.getMessage();
            return message != null ? message : "An unexpected error occurred. Please try again.";
        }
    }

    public String getPromptLabel() {
        return state == State.SIGNED_OUT ? "SIGNED_OUT" : "SIGNED_IN";
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
