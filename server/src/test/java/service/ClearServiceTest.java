package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ClearServiceTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    private ClearService clearService;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO,authDAO,gameDAO);
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("bobert", "password", "bobert@email.com"));

        clearService.clear();

        assertNull(userDAO.getUser("bobert"));
    }
}
