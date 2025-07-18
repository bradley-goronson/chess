package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class GameMemoryDAO implements GameDataAccess {
    private HashMap<Integer, GameData> gameMap = new HashMap<>();
    Integer nextID = 1;

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

    public void clearGames() {
        gameMap.clear();
    }

    public ArrayList<GameData> getAllGames() {
        return new ArrayList<>(gameMap.values());
    }

    public int size() {
        return gameMap.size();
    }
}
