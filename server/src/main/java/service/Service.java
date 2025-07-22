package service;

import dataaccess.*;

public class Service {
    public static UserDataAccess userDAO = new MySQLUserDAO();
    public static GameDataAccess gameDAO = new MySQLGameDAO();
    public static AuthDataAccess authDAO = new MySQLAuthDAO();

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
