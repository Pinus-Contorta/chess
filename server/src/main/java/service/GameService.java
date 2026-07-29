package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
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

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (authToken == null || auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (request.playerColor() == null ||
                (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK"))) {
            throw new DataAccessException("Error: bad request");
        }

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = auth.username();

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: white already taken");
            }
            GameData updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(updated);
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: black already taken");
            }
            GameData updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            gameDAO.updateGame(updated);
        }
    }
}
