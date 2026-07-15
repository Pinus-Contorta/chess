package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();
    //For making unique ID later on.
    private int nextID = 1;

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(),game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if(!games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: game not found.");
        }
        games.put(game.gameID(), game);
    }
}
