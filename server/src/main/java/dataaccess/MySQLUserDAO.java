package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDataAccess {
    public void addUser(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        try {
            getUser(user.username());
            throw new AlreadyTakenException("Error: already taken");
        } catch (DataAccessException ex) {
            String hashedPassword = hashUserPassword(user.password());
            String sql =
                    "insert into users(" +
                    "username, password, email)" +
                    "values('" + user.username() +
                    "','" + hashedPassword +
                    "','" + user.email() + "')";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("failed to add user", e);
            }
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "select * from users where username='" + username + "'";
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet queryResult = statement.executeQuery();
            if (!queryResult.next()) {
                throw new DataAccessException("User not found");
            }
            String pulledUsername = queryResult.getString("username");
            String pulledPassword = queryResult.getString("password");
            String pulledEmail = queryResult.getString("email");
            return new UserData(pulledUsername, pulledPassword, pulledEmail);
        } catch (SQLException e) {
            throw new DataAccessException("failed to get user", e);
        }
    }

    public void clearUsers() {
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "delete from users";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (DataAccessException | SQLException ex) {
            System.out.println("clear failed");
        }
    }

    public int size() throws DataAccessException {
        String sql = "select count(*) from users";
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet queryResult = statement.executeQuery();
            queryResult.next();
            return queryResult.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("failed to get row count", e);
        }
    }

    private String hashUserPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
}
