package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import model.UserData;
import model.AuthData;
import org.eclipse.jetty.server.Authentication;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if(request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if(UserDAO.getUser(request.username()) != null) {
            throw new DataAccessException("Error: username already taken");
        }

        userDAO.createUser(new UserData(request.username(), request.password(), request.email()));

        String authToken =  java.util.UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(request.username(), authToken);
    }
}
