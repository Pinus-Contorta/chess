package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {

    private GameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        GameData game = new GameData(1, null, null, "Test Game", new ChessGame());

        gameDAO.createGame(game);

        GameData result = gameDAO.getGame(1);
        assertNotNull(result);
        assertEquals("Test Game", result.gameName());
    }

    @Test
    public void createGameDuplicateIDFails() throws DataAccessException {
        GameData game = new GameData(1, null, null, "Test Game", new ChessGame());
        gameDAO.createGame(game);

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new GameData(1, "alice", "bobert", "Test Game", chessGame));

        GameData result = gameDAO.getGame(1);

        assertNotNull(result);
        assertEquals(1, result.gameID());
        assertEquals("alice", result.whiteUsername());
        assertEquals("bobert", result.blackUsername());
        assertEquals("Test Game", result.gameName());
        assertEquals(chessGame, result.game());
    }

    @Test
    public void getGameNotFoundReturnsNull() throws DataAccessException {
        GameData result = gameDAO.getGame(999);

        assertNull(result);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null, null, "Game One", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "Game Two", new ChessGame()));

        Collection<GameData> games = gameDAO.listGames();

        assertEquals(2, games.size());
    }

    @Test
    public void listGamesEmptyWhenNoneExist() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null, null, "Test Game", new ChessGame()));

        GameData updated = new GameData(1, "alice", null, "Test Game", new ChessGame());
        gameDAO.updateGame(updated);

        GameData result = gameDAO.getGame(1);
        assertEquals("alice", result.whiteUsername());
    }

    @Test
    public void updateGameNonexistentFails() {
        GameData game = new GameData(999, "alice", null, "Ghost Game", new ChessGame());

        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(game));
    }

    @Test
    public void clearRemovesAllGames() throws DataAccessException {
        gameDAO.createGame(new GameData(1, null, null, "Game One", new ChessGame()));
        gameDAO.createGame(new GameData(2, null, null, "Game Two", new ChessGame()));

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }
}