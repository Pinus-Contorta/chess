package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS `user` (
                `username` VARCHAR(255) NOT NULL,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`username`)
                )
            """
    };

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE `user`");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement =  "INSERT INTO `user` (username, password, email) VALUES (?,?,?)";
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM `user` WHERE username=?";

        try (var connection = DatabaseManager.getConnection(); var prepStatement = connection.prepareStatement(statement)) {
            prepStatement.setString(1, username);

            try(var resultSet = prepStatement.executeQuery()) {
                if(resultSet.next()) {
                    return new UserData(resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("email"));
                }
            }
        }catch (SQLException exception) {
            throw new DataAccessException("Error: could not read user data", exception);
        }
        return null;
    }

    private void executeUpdate(String statement, String... params) throws DataAccessException {
        SQLAuthDAO.updateExecutor(statement, params);
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try(var connection = DatabaseManager.getConnection()) {
            for (var statement : CREATE_STATEMENTS) {
                try (var prepStatement = connection.prepareStatement(statement)) {
                    prepStatement.executeUpdate();
                }
            }
        } catch (SQLException exception){
            throw new DataAccessException("Error: unable to configure the database", exception);
        }
    }
}
