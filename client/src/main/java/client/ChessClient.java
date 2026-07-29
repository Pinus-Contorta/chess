package client;

public class ChessClient {

    private State state = State.SIGNED_OUT;
    private String authToken;
    private String username;

    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;

    public ChessClient(String serverURL, PreLoginClient preLoginClient, PostLoginClient postLoginClient) {
        this.preLoginClient = preLoginClient;
        this.postLoginClient = postLoginClient;
        //TODO:Impliment ServerFacade
        ServerFacade serverFacade = new ServerFacade(serverURL);
        preLoginClient = new PreLoginClient(serverFacade, this);
        postLoginClient = new PostLoginClient(serverFacade, this)
    }

    public String eval(String inputString) {
        try {
            return switch (state) {
                case SIGNED_OUT -> preLoginClient.eval(inputString);
                case SIGNED_IN -> postLoginClient.eval(inputString);
            };
        } catch (Exception exception) {
            return "Error: " + exception.getMessage();
        }
    }
}
