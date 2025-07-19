package server.listgames;

import dataaccess.UnauthorizedException;
import service.Service;

import java.util.ArrayList;

public class ListGamesService extends Service {
    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        ListGamesResult listGamesResult = new ListGamesResult();
        listGamesResult.setGamesArray(new ArrayList<>());
        String authToken = listGamesRequest.getAuthToken();

        if (authToken == null) {
            listGamesResult.setStatusCode(400);
            listGamesResult.setMessage("Error: bad request");
            return listGamesResult;
        }

        try {
            authDAO.getAuth(authToken);
            listGamesResult.setGamesArray(gameDAO.getAllGames());
            listGamesResult.setStatusCode(200);
        } catch (UnauthorizedException e) {
            listGamesResult.setStatusCode(401);
            listGamesResult.setMessage("Error: unauthorized");
        }
        return listGamesResult;
    }
}
