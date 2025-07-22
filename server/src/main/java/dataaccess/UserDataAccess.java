package dataaccess;

import model.UserData;

public interface UserDataAccess {
    void addUser(UserData user) throws AlreadyTakenException, DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clearUsers();

    int size();
}
