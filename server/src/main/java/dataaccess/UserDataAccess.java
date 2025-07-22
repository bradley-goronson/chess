package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDataAccess {
    void addUser(UserData user) throws AlreadyTakenException, DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clearUsers();

    int size() throws DataAccessException;
}
