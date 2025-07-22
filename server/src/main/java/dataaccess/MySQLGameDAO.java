package dataaccess;

import chess.ChessGame;
import model.GameData;

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

    }

    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> gamesArray = new ArrayList<>();

        return gamesArray;
    }

    public int size() {
        return 1;
    }
}
