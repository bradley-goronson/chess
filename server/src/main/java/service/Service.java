package service;

import dataaccess.UserDataAccess;
import dataaccess.UserMemoryDAO;

public class Service {
    public static UserDataAccess userDAO = new UserMemoryDAO();

    public UserDataAccess getUserDAO() {
        return userDAO;
    }
}
