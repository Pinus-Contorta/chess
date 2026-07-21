package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "bobert");

        authDAO.createAuth(auth);

        AuthData result = authDAO.getAuth("token123");
        assertEquals(auth, result);
    }

    @Test
    public void createAuthDuplicateTokenFails() throws DataAccessException {
        AuthData auth = new AuthData("token123", "bobert");
        authDAO.createAuth(auth);

        // authToken is the primary key, so re-inserting it should fail
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "bobert"));

        AuthData result = authDAO.getAuth("token123");

        assertNotNull(result);
        assertEquals("bobert", result.username());
    }

    @Test
    public void getAuthNotFoundReturnsNull() throws DataAccessException {
        AuthData result = authDAO.getAuth("nonexistentToken");

        assertNull(result);
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "bobert"));

        authDAO.deleteAuth("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    public void deleteAuthNonexistentTokenDoesNotThrow() {
        // deleting a token that was never there isn't an error condition here —
        // SQL DELETE simply matches zero rows
        assertDoesNotThrow(() -> authDAO.deleteAuth("neverExisted"));
    }

    @Test
    public void clearRemovesAllAuthData() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "bobert"));
        authDAO.createAuth(new AuthData("token456", "alice"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token123"));
        assertNull(authDAO.getAuth("token456"));
    }
}