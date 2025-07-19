package server.clear;

import service.Service;

public class ClearService extends Service {

    public ClearResult clear() {
        ClearResult clearResult = new ClearResult();
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuth();
        clearResult.setStatusCode(200);

        return clearResult;
    }
}
