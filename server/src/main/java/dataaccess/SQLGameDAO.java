package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    static private final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS game (
                `gameID` INT NOT NULL,
                 `whiteUsername` VARCHAR(255),
                 `blackUsername` VARCHAR(255),
                 `gameName` VARCHAR(255) NOT NULL,
                 `game` TEXT NOT NULL,
                 PRIMARY KEY (`gameID`)
                )
            """

    };

    private static final Gson GSON = new Gson();

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE game");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var statement =  "INSERT INTO `user` (" +
                "gameID, " +
                "whiteUsername, " +
                "blackUsername" +
                "gameName" +
                "game) VALUES (?,?,?,?,?)";
        var gameJSON = GSON.toJson(game.game());
        executeUpdate(statement, String.valueOf(game.gameID()), game.whiteUsername(), game.blackUsername(),game.gameName(), gameJSON);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM `game` WHERE gameID=?";
        try (var connection = DatabaseManager.getConnection(); var prepStatement = connection.prepareStatement(statement)) {
            prepStatement.setInt(1, gameID);
            try (var resultSet = prepStatement.executeQuery()) {
                if (resultSet.next()) {
                    return readGame(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to read game data", ex);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM `game`";
        var result = new ArrayList<GameData>();
        try (var connection = DatabaseManager.getConnection(); var prepStatement = connection.prepareStatement(statement); var resultSet = prepStatement.executeQuery()) {
            while (resultSet.next()) {
                result.add(readGame(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to list games", ex);
        }
        return result;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE `game` SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        var gameJSON = GSON.toJson(game.game());
        try (var connection = DatabaseManager.getConnection(); var prepStatement = connection.prepareStatement(statement)) {
            prepStatement.setString(1, game.whiteUsername());
            prepStatement.setString(2, game.blackUsername());
            prepStatement.setString(3, game.gameName());
            prepStatement.setString(4, gameJSON);
            prepStatement.setInt(5, game.gameID());
            var rowsAffected = prepStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: game not found");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: unable to update game", ex);
        }
    }

    private void executeUpdate(String statement, String... params) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection(); var prepStatement = connection.prepareStatement(statement)){
            for (int i = 0; i < params.length; i++) {
                prepStatement.setString(i + 1, params[i]);
            }
            prepStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error: database could not be updated", exception);
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try(var connection = DatabaseManager.getConnection()) {
            for (var statement : CREATE_STATEMENTS) {
                try (var prepStatement = connection.prepareStatement(statement)) {
                    prepStatement.executeUpdate();
                }
            }
        } catch (SQLException exception){
            throw new DataAccessException("Error: unable to configure the database", exception);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var chessGame = GSON.fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                chessGame);
    }
}
