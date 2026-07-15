package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    //Keeps states clean.
    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    //REGISTRATION TESTS

    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bobert", "password", "bobert@email.com");

        RegisterResult result = userService.register(request);

        assertEquals("bobert", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerDuplicateUsernameFails() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("bobert", "password", "bobert@email.com");
        userService.register(request); // first registration succeeds

        assertThrows(DataAccessException.class, () -> userService.register(request));
    }

    //LOGIN TESTS

    @Test
    public void loginSuccess() throws DataAccessException {
        userService.register(new RegisterRequest("bobert", "password", "bobert@email.com"));

        LoginResult result = userService.login(new LoginRequest("bobert", "password"));

        assertEquals("bobert", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginWrongPasswordFails() throws DataAccessException {
        userService.register(new RegisterRequest("bobert", "password", "bobert@email.com"));

        assertThrows(DataAccessException.class,
                () -> userService.login(new LoginRequest("bobert", "wrongpassword")));
    }

    //LOGOUT TESTS

    @Test
    public void logoutSucess() throws DataAccessException {
        RegisterResult registerResult = userService.register(new RegisterRequest("bobert", "password", "bobert@email.com"));

        userService.logout(registerResult.authToken());

        assertThrows(DataAccessException.class, () -> userService.logout(registerResult.authToken()));
    }

    @Test
    public void logoutBadTokenFail() {
        assertThrows(DataAccessException.class, () -> userService.logout("insert bad token here"));
    }
}