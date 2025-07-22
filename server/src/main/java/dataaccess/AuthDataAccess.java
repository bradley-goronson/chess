package dataaccess;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

public interface AuthDataAccess {
    String addAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws UnauthorizedException, DataAccessException;

    void removeAuth(String authToken) throws UnauthorizedException, DataAccessException;

    void clearAuth();

    int size() throws DataAccessException;
}
