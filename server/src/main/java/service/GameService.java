package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
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

    //Phase 6 Methods

    private AuthData validateAuth(String authToken) throws DataAccessException {
        if(authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException(("Error: unauthorized"));
        }

        return authDAO.getAuth(authToken);
    }

    private ChessGame.TeamColor colorOf(GameData gameData, String username) {
        return username.equals(gameData.whiteUsername()) ? ChessGame.TeamColor.WHITE :
                username.equals(gameData.blackUsername()) ? ChessGame.TeamColor.BLACK : null;
    }

    public GameData connect(String authToken, int gameID) throws DataAccessException {

        validateAuth(authToken);

        GameData game = gameDAO.getGame(gameID);

        if(game == null) {
            throw new DataAccessException("Error: bad request");
        }

        return game;
    }

    public MoveResult makeMove(String authToken, int gameID, ChessMove move) throws DataAccessException {

        AuthData authData = validateAuth(authToken);
        GameData game = gameDAO.getGame(gameID);

        if(game == null) {
            throw new DataAccessException("Error: bad request");
        }
        if(game.game().isGameOver()) {
            throw new DataAccessException("Error: game is already ended");
        }

        ChessGame.TeamColor playerColor = colorOf(game, authData.username());
        if(playerColor == null) {
            throw new DataAccessException("Error: observers cannot make moves");
        }
        if(game.game().getTeamTurn() != playerColor) {
            throw new DataAccessException("Error: it is not currently your turn");
        }

        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException exception) {
            throw new DataAccessException("Error: " + exception.getMessage());
        }

        gameDAO.updateGame(game);
        return new MoveResult(game, playerColor);
    }

    public GameData resign(String authToken, int gameID) throws DataAccessException {
        AuthData auth = validateAuth(authToken);
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (!auth.username().equals(game.whiteUsername()) && !auth.username().equals(game.blackUsername())) {
            throw new DataAccessException("Error: observers cannot resign");
        }
        if (game.game().isGameOver()) {
            throw new DataAccessException("Error: game is already over");
        }

        game.game().setGameOver(true);
        gameDAO.updateGame(game);
        return game;
    }

    public GameData leave(String authToken, int gameID) throws DataAccessException {
        AuthData auth = validateAuth(authToken);
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String white = auth.username().equals(game.whiteUsername()) ? null : game.whiteUsername();
        String black = auth.username().equals(game.blackUsername()) ? null : game.blackUsername();

        GameData updated = new GameData(game.gameID(), white, black, game.gameName(), game.game());
        gameDAO.updateGame(updated);
        return updated;
    }

}
