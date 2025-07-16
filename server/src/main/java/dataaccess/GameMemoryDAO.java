package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class GameMemoryDAO implements GameDataAccess {
    private HashMap<Integer, GameData> gameMap = new HashMap<>();
    Integer nextID = 0;

    public Integer addGame(String gameName) {
        Integer gameID = nextID;
        gameMap.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        nextID++;
        return gameID;
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        GameData game = gameMap.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    public void updateGame(Integer gameID, GameData newGame) {
        gameMap.put(gameID, newGame);
    }

    public void removeGame(Integer gameID) throws DataAccessException {
        gameMap.remove(gameID);
    }

    public void clearGames() {
        gameMap.clear();
    }

    public int size() {
        return gameMap.size();
    }
}
