package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UserNotFoundException;
import model.UserData;

public interface UserDataAccess {
    void addUser(UserData user) throws AlreadyTakenException, DataAccessException;

    UserData getUser(String username) throws UserNotFoundException, DataAccessException;

    void clearUsers() throws DataAccessException;

    int size() throws DataAccessException;
}
