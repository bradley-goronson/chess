package dataaccess;

import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    Integer addGame(String gameName);

    GameData getGame(Integer gameID) throws GameNotFoundException;

    void updateGame(Integer gameID, GameData game);

    void clearGames();

    ArrayList<GameData> getAllGames();

    int size();
}