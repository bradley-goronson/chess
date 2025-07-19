package server.listgames;

import server.Handler;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends Handler {
    public Object handle(Request req, Response res) {
        ListGamesRequest listGamesRequest = new ListGamesRequest();
        listGamesRequest.setAuthorization(req.headers("Authorization"));
        ListGamesResult listGamesResult = new ListGamesService().listGames(listGamesRequest);
        res.status(listGamesResult.getStatusCode());
        return serializer.toJson(listGamesResult);
    }
}
