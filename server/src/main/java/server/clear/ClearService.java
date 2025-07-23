package server.clear;

import dataaccess.exceptions.DataAccessException;
import service.Service;

public class ClearService extends Service {

    public ClearResult clear() {
        ClearResult clearResult = new ClearResult();
        try {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuth();
            clearResult.setStatusCode(200);
        } catch (DataAccessException e) {
            clearResult.setStatusCode(500);
            clearResult.setMessage("Error : lost connection when clearing");
        }

        return clearResult;
    }
}
