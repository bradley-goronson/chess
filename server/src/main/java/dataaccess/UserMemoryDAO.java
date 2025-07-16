package dataaccess;

import model.UserData;
import java.util.HashMap;

public class UserMemoryDAO implements UserDataAccess {
    private HashMap<String, UserData> userMap = new HashMap<>();

    public void addUser(UserData user) throws AlreadyTakenException {
        try {
            getUser(user.username());
            throw new AlreadyTakenException("Username already taken");
        } catch (DataAccessException ex) {
            userMap.put(user.username(), user);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = userMap.get(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }
        return user;
    }

    public void removeUser(String username) throws DataAccessException {
        getUser(username);
        userMap.remove(username);
    }

    public void clearUsers() {
        userMap.clear();
    }

    public int size() {
        return userMap.size();
    }
}
