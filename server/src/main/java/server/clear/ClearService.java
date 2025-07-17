package server.clear;

import server.Result;
import service.Service;

public class ClearService extends Service {

    public ClearResult clear() {
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuth();

        return new ClearResult(200);
    }
}
