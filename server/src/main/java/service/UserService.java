package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;

import model.UserData;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        //Checks for bad registration request.
        if(request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        //Checks for duplicate user.
        if(userDAO.getUser(request.username()) != null) {
            throw new DataAccessException("Error: username already taken");
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));

        String authToken =  java.util.UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login (LoginRequest request) throws DataAccessException {
        //Checks for bad request.
        if(request.username() == null || request.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        //Checks for bad credentials.

        UserData user = userDAO.getUser(request.username());

        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new DataAccessException("Error: invalid user");
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, request.username()));

        return new LoginResult(request.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        if(authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
