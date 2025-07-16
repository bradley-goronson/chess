package service;

import dataaccess.*;

public class Service {
    public static UserDataAccess userDAO = new UserMemoryDAO();
    public static GameDataAccess gameDAO = new GameMemoryDAO();
    public static AuthDataAccess authDAO = new AuthMemoryDAO();

    public UserDataAccess getUserDAO() {
        return userDAO;
    }

    public GameDataAccess getGameDAO() {
        return gameDAO;
    }

    public AuthDataAccess getAuthDAO() {
        return authDAO;
    }
}
