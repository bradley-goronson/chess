package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDataAccess {
    public String addAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        String sql =
                "insert into auth(" +
                        "authToken, username)" +
                        "values('" + authData.authToken() +
                        "','" + authData.username() + "')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to add auth", e);
        }
        return authToken;
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException, DataAccessException {
        String sql = "select * from auth where authToken='" + authToken + "'";
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet queryResult = statement.executeQuery();
            queryResult.next();
            String pulledAuthToken = queryResult.getString("authToken");
            String pulledUsername = queryResult.getString("username");
            return new AuthData(pulledAuthToken, pulledUsername);
        } catch (SQLException e) {
            throw new DataAccessException("failed to get auth", e);
        }
    }

    public void removeAuth(String authToken) throws UnauthorizedException, DataAccessException {
        String sql = "delete from auth where authToken='" + authToken + "'";
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to remove auth", e);
        }
    }

    public void clearAuth() {
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "delete from auth";
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
        String sql = "select count(*) from auth";
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet queryResult = statement.executeQuery();
            queryResult.next();
            return queryResult.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("failed to get row count", e);
        }
    }
}
