package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        List<GameTag> summaries = new ArrayList<>();
        for (GameData game : gameDAO.listGames()) {
            summaries.add(new GameTag(
                    game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }

        return new ListGamesResult(summaries);

    }
}
