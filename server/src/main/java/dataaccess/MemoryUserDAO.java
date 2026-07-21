package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: already taken");
        }
        var hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var userToLog = new UserData(user.username(), hashedPassword, user.email());
        users.put(user.username(), userToLog);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }
}