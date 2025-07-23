package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

public class MySQLGameDAO implements GameDataAccess {
    public Integer addGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new BadRequestException("Error: bad request");
        }
        int gameID;
        String sql =
                "insert into games(whiteUsername, blackUsername, gameName, game)" +
                        "values(?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, gameName);
            String jsonChessGame = serializeChessGame(new ChessGame());
            preparedStatement.setString(4, jsonChessGame);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            gameID = generatedKeys.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("failed to add game", e);
        }
        return gameID;
    }

public GameData getGame(Integer gameID) throws GameNotFoundException, DataAccessException {
    String sql = "select * from games where gameID = ?";
    try (var conn = DatabaseManager.getConnection();
         PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setInt(1, gameID);
        ResultSet queryResult = statement.executeQuery();
        if (!queryResult.next()) {
            throw new GameNotFoundException("Error: bad request");
        }
        String pulledWhiteUsername = queryResult.getString("whiteUsername");
        String pulledBlackUsername = queryResult.getString("blackUsername");
        String pulledGameName = queryResult.getString("gameName");
        String pulledGameJSON = queryResult.getString("game");
        ChessGame pulledGame = new Gson().fromJson(pulledGameJSON, ChessGame.class);
        return new GameData(gameID, pulledWhiteUsername, pulledBlackUsername, pulledGameName, pulledGame);
    } catch (SQLException e) {
        throw new DataAccessException("failed to get game", e);
    }
}

public void updateGame(Integer gameID, GameData game) throws DataAccessException {
    getGame(gameID);
    String sql =
            "update games set whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? where gameID = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, game.whiteUsername());
        preparedStatement.setString(2, game.blackUsername());
        preparedStatement.setString(3, game.gameName());
        preparedStatement.setString(4, serializeChessGame(game.game()));
        preparedStatement.setInt(5, gameID);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        throw new DataAccessException("failed to update game", e);
    }
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

public ArrayList<GameData> getAllGames() throws DataAccessException {
    ArrayList<GameData> gamesArray = new ArrayList<>();

    String sql = "select * from games";
    try (var conn = DatabaseManager.getConnection();
         PreparedStatement statement = conn.prepareStatement(sql)) {
        ResultSet queryResult = statement.executeQuery();
        while (queryResult.next()) {
            int pulledGameID = queryResult.getInt("gameID");
            String pulledWhiteUsername = queryResult.getString("whiteUsername");
            String pulledBlackUsername = queryResult.getString("blackUsername");
            String pulledGameName = queryResult.getString("gameName");
            String pulledChessGameJSON = queryResult.getString("game");
            ChessGame pulledChessGame = deSerializeChessGame(pulledChessGameJSON);
            GameData pulledGameData = new GameData(
                    pulledGameID,
                    pulledWhiteUsername,
                    pulledBlackUsername,
                    pulledGameName,
                    pulledChessGame
            );
            gamesArray.add(pulledGameData);
        }
    } catch (SQLException e) {
        throw new DataAccessException("failed to get user", e);
    }
    return gamesArray;
}

public int size() throws DataAccessException {
    String sql = "select count(*) from games";
    try (var conn = DatabaseManager.getConnection();
         PreparedStatement statement = conn.prepareStatement(sql)) {
        ResultSet queryResult = statement.executeQuery();
        queryResult.next();
        return queryResult.getInt(1);
    } catch (SQLException e) {
        throw new DataAccessException("failed to get row count", e);
    }
}

private String serializeChessGame(ChessGame chessGame) {
        return new Gson().toJson(chessGame);
}

    private ChessGame deSerializeChessGame(String chessGameJSON) {
        return new Gson().fromJson(chessGameJSON, ChessGame.class);
    }
}