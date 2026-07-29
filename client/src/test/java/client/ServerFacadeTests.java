package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        serverFacade.clear();
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }


    //Registration Tests
    @Test
    public void registrationSuccess() throws Exception {
        AuthData authData = serverFacade.register("Bobbert", "BobbertIsCool", "Bobbert@gmail.com");

        assertNotNull(authData.authToken());
        assertTrue(authData.authToken().length() > 10);
        assertEquals("Bobbert", authData.username());
    }

    @Test
    public void registerDuplicateUsernameFailure() throws Exception {
        serverFacade.register("Bobbert", "BobbertIsCool", "Bobbert@gmail.com");

        assertThrows(ResponseException.class, () ->
                serverFacade.register("Bobbert", "BobbertIsDifferent", "Bobbert@outlook.com"));
    }

    //Login Tests
    @Test
    public void loginSuccessful() throws Exception {
        serverFacade.register("Bobbert", "BobbertIsCool", "Bobbert@gmail.com");

        AuthData authData = serverFacade.login("Bobbert", "BobbertIsCool");

        assertNotNull(authData.authToken());
        assertEquals("Bobbert", authData.username());
    }

    @Test
    public void loginWrongPasswordFailure() throws Exception {
        serverFacade.register("Bobbert", "BobbertIsCool", "Bobbert@gmail.com");

        assertThrows(ResponseException.class, () ->
                serverFacade.login("Bobbert", "BobbertIsWrong"));
    }

    //Logout Tests

    @Test
    public void logoutSuccessful() throws Exception {
        AuthData authData = serverFacade.register("Bobbert", "BobbertIsCool", "Bobbert@gmail.com");

        assertDoesNotThrow(() -> serverFacade.logout(authData.authToken()));
    }

    @Test
    public void logoutBadTokenFailure() throws Exception {
        assertThrows(ResponseException.class, () ->
                serverFacade.logout("Bad_Token"));
    }

    //Game Creation Tests
    @Test
    public void createGameSuccess() throws Exception {
        AuthData authData = serverFacade.register("player1", "password", "p1@email.com");

        int gameID = serverFacade.createGame(authData.authToken(), "Test Game");

        assertTrue(gameID > 0);
    }

    @Test
    public void createGameBadAuthTokenFails() {
        assertThrows(ResponseException.class, () ->
                serverFacade.createGame("bad-token", "Test Game"));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        AuthData authData = serverFacade.register("player1", "password", "p1@email.com");
        serverFacade.createGame(authData.authToken(), "Test Game");

        List<GameSummary> games = serverFacade.listGames(authData.authToken());

        assertEquals(1, games.size());
        assertEquals("Test Game", games.get(0).gameName());
    }

    @Test
    public void listGamesBadAuthTokenFails() {
        assertThrows(ResponseException.class, () -> serverFacade.listGames("bad-token"));
    }

    @Test
    public void joinGameSuccess() throws Exception {
        AuthData authData = serverFacade.register("player1", "password", "p1@email.com");
        int gameID = serverFacade.createGame(authData.authToken(), "Test Game");

        assertDoesNotThrow(() -> serverFacade.joinGame(authData.authToken(), "WHITE", gameID));
    }

    @Test
    public void joinGameColorAlreadyTakenFails() throws Exception {
        AuthData player1 = serverFacade.register("player1", "password", "p1@email.com");
        AuthData player2 = serverFacade.register("player2", "password", "p2@email.com");
        int gameID = serverFacade.createGame(player1.authToken(), "Test Game");

        serverFacade.joinGame(player1.authToken(), "WHITE", gameID);

        assertThrows(ResponseException.class, () ->
                serverFacade.joinGame(player2.authToken(), "WHITE", gameID));
    }

    @Test
    public void clearSuccess() throws Exception {
        serverFacade.register("player1", "password", "p1@email.com");

        assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    public void clearUnreachableServerFails() {
        ServerFacade badFacade = new ServerFacade(1);

        assertThrows(ResponseException.class, badFacade::clear);
    }
}


