package server.joingame;

import server.Handler;
import spark.Request;
import spark.Response;

public class JoinGameHandler extends Handler {
    public Object handle(Request req, Response res) {
        JoinGameRequest joinGameRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
        joinGameRequest.setAuthorization(req.headers("Authorization"));
        JoinGameResult joinGameResult = new JoinGameService().joinGame(joinGameRequest);
        res.status(joinGameResult.getStatusCode());
        return serializer.toJson(joinGameResult);
    }
}
