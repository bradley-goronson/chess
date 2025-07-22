package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    String addAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws UnauthorizedException;

    void removeAuth(String authToken) throws UnauthorizedException;

    void clearAuth();

    int size() throws DataAccessException;
}
