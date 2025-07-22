package dataaccess;

import dataaccess.exceptions.UnauthorizedException;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthMemoryDAO implements AuthDataAccess {
    private HashMap<String, AuthData> authMap = new HashMap<>();

    public String addAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authMap.put(authToken, authData);
        return authToken;
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException {
        AuthData authData = authMap.get(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return authData;
    }

    public void removeAuth(String authToken) {
        authMap.remove(authToken);
    }

    public void clearAuth() {
        authMap.clear();
    }

    public int size() {
        return authMap.size();
    }
}
