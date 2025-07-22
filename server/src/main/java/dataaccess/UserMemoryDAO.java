package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import model.UserData;
import java.util.HashMap;

public class UserMemoryDAO implements UserDataAccess {
    private HashMap<String, UserData> userMap = new HashMap<>();

    public void addUser(UserData user) throws AlreadyTakenException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new BadRequestException("Error: bad request");
        }

        try {
            getUser(user.username());
            throw new AlreadyTakenException("Error: already taken");
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

    public void clearUsers() {
        userMap.clear();
    }

    public int size() {
        return userMap.size();
    }
}
