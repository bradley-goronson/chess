package dataaccess;

import model.UserData;

public interface UserDataAccess {
    public void addUser(UserData user) throws AlreadyTakenException;

    public UserData getUser(String username) throws DataAccessException;

    public void updateUser(String username, UserData user) throws DataAccessException;

    public void removeUser(String username) throws DataAccessException;

    public void clearUsers();

    public int size();
}
