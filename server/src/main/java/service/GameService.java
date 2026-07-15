package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    //AtomicInteger future-proofs us for concurrent ID generation
    private final AtomicInteger nextGameID = new AtomicInteger(1);

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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if(authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if(request.gameName() == null || request.gameName().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }

        int gameID = nextGameID.getAndIncrement();

        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(game);

        return new CreateGameResult(gameID);
    }
}
