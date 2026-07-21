package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {


    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                'authToken' VARCHAR(255) NOT NULL,
                'username' VARCHAR(255) NOTE NULL,
                PRIMARY KEY ('authToken')
                )
            """
    };

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE auth");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement =  "INSERT INTO auth (authToken, username) VALUES (?,?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    private void executeUpdate(String statement, String... params) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection(); var prepStatement = connection.prepareStatement(statement)){
            for (int i = 0; i < params.length; i++) {
                prepStatement.setString(i + 1, params[i]);
            }
            prepStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error: database could not be updated", exception);
        }
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
