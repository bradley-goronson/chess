package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDataAccess {
    public String addAuth(String username) {
        return "";
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException {
        return new AuthData("a", "a");
    }

    public void removeAuth(String authToken) throws UnauthorizedException {

    }

    public void clearAuth() {

    }

    public int size() {
        return 1;
    }
}
