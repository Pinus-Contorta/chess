package dataaccess;

import model.UserData;

public interface UserDAO {

    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;

    static UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
