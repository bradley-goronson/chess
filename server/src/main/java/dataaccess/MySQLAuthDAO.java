package dataaccess;

import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDataAccess {
    public String addAuth(String username) {
        return "";
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException {
        return new AuthData("a", "a");
    }

    public void removeAuth(String authToken) throws UnauthorizedException {

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

    public int size() {
        return 1;
    }
}
