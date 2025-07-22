package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLGameDAO implements GameDataAccess {
    public Integer addGame(String gameName) {
        return 1;
    }

    public GameData getGame(Integer gameID) throws GameNotFoundException {
        return new GameData(1, "a", "b", "ab", new ChessGame());
    }

    public void updateGame(Integer gameID, GameData game) {

    }

    public void clearGames() {
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "delete from games";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (DataAccessException | SQLException ex) {
            System.out.println("clear failed");
        }
    }

    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> gamesArray = new ArrayList<>();

        return gamesArray;
    }

    public int size() {
        return 1;
    }
}
