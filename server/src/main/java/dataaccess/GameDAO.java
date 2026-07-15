package dataaccess;

import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException;

    GameData getGame(int GameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;
}
