package server.listGames;

import server.Handler;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class ListGamesHandler extends Handler {
    public Object handle(Request req, Response res) {
        ListGamesRequest listGamesRequest = new ListGamesRequest();
        listGamesRequest.setAuthorization(req.headers("Authorization"));
        ListGamesResult listGamesResult = new ListGamesService().listGames(listGamesRequest);
        res.status(listGamesResult.getStatusCode());
        Object json = serializer.toJson(listGamesResult);
        return json;
    }
}
