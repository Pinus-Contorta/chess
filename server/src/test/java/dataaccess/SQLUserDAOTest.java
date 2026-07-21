package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("bobert", "password", "bobert@email.com");

        userDAO.createUser(user);

        UserData result = userDAO.getUser("bobert");
        assertNotNull(result);
        assertEquals("bobert", result.username());
        assertEquals("bobert@email.com", result.email());
    }

    @Test
    public void createUserDuplicateUsernameFails() throws DataAccessException {
        UserData user = new UserData("bobert", "password", "bobert@email.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("bobert", "password", "bobert@email.com"));

        UserData result = userDAO.getUser("bobert");

        assertNotNull(result);
        assertTrue(BCrypt.checkpw("password", result.password()));
    }

    @Test
    public void getUserNotFoundReturnsNull() throws DataAccessException {
        UserData result = userDAO.getUser("nonexistentUser");

        assertNull(result);
    }

    @Test
    public void clearRemovesAllUsers() throws DataAccessException {
        userDAO.createUser(new UserData("bobert", "password", "bobert@email.com"));
        userDAO.createUser(new UserData("alice", "password2", "alice@email.com"));

        userDAO.clear();

        assertNull(userDAO.getUser("bobert"));
        assertNull(userDAO.getUser("alice"));
    }
}