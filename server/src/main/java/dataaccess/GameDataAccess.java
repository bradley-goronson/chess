package dataaccess;

import model.GameData;

public interface GameDataAccess {
    Integer addGame(String gameName);

    GameData getGame(Integer gameID) throws DataAccessException;

    void updateGame(Integer gameID, GameData game);

    void removeGame(Integer gameID) throws DataAccessException;

    void clearGames();

    int size();
}