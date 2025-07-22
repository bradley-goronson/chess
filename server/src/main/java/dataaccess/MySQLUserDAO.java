package dataaccess;

import model.UserData;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDataAccess {
    public void addUser(UserData user) {

    }

    public UserData getUser(String username) {
        return null;
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
            System.out.println("bad");
        }
    }

    public int size() {
        return 1;
    }
}
