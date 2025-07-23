package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    Integer addGame(String gameName) throws DataAccessException;

    GameData getGame(Integer gameID) throws GameNotFoundException, DataAccessException;

    void updateGame(Integer gameID, GameData game);

    void clearGames();

    ArrayList<GameData> getAllGames() throws DataAccessException;

    int size() throws DataAccessException;
}