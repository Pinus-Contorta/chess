package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {


    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private String authToken;
    private GameService gameService;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        gameService = new GameService(gameDAO, authDAO);

        authToken = "test-token";
        authDAO.createAuth(new AuthData(authToken, "bobert"));
    }

    //LIST TESTS

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null,null,"thisGame", new ChessGame()));

        ListGamesResult result = gameService.listGames(authToken);

        //Checks list size
        assertEquals(1,result.games().size());
        //Checks name of 1st item
        assertEquals("thisGame", result.games().get(0).gameName());
    }

    @Test
    public void listGamesUnauthorizedFail() {
        assertThrows(DataAccessException.class, () -> gameService.listGames("bogus-token"));
    }

    //CREATE GAME TESTS

    @Test
    public void createGameSuccess() throws DataAccessException {
        CreateGameResult result = gameService.createGame(authToken, new CreateGameRequest("thisGame"));

        assertTrue(result.gameID() > 0);
        assertNotNull(gameDAO.getGame(result.gameID()));
    }

    @Test
    public void createNameBadNameFail() {
        assertThrows(DataAccessException.class, () -> gameService.createGame(authToken,new CreateGameRequest(null)));
    }

}
