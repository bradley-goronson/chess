package server.createGame;

import server.Handler;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends Handler {
    public Object handle(Request req, Response res) {
        CreateGameRequest createGameRequest = serializer.fromJson(req.body(), CreateGameRequest.class);
        createGameRequest.setAuthorization(req.headers("Authorization"));
        CreateGameResult createGameResult = new CreateGameService().addGame(createGameRequest);
        res.status(createGameResult.getStatusCode());
        return serializer.toJson(createGameResult);
    }
}
