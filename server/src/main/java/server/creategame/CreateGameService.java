package server.creategame;

import dataaccess.UnauthorizedException;
import service.Service;

public class CreateGameService extends Service {
    public CreateGameResult addGame(CreateGameRequest createGameRequest) {
        CreateGameResult createGameResult = new CreateGameResult();
        String authToken = createGameRequest.getAuthToken();
        String gameName = createGameRequest.getGameName();

        if (authToken == null || gameName == null) {
            createGameResult.setStatusCode(400);
            createGameResult.setMessage("Error: bad request");
            return createGameResult;
        }

        try {
            authDAO.getAuth(authToken);
            Integer gameID = gameDAO.addGame(gameName);
            createGameResult.setGameID(gameID);
            createGameResult.setStatusCode(200);
        } catch (UnauthorizedException e) {
            createGameResult.setStatusCode(401);
            createGameResult.setMessage(e.getMessage());
        }
        return createGameResult;
    }
}
