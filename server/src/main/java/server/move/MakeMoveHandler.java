package server.move;

import server.Handler;
import spark.Request;
import spark.Response;

public class MakeMoveHandler extends Handler {
    public Object handle(Request req, Response res) {
        MakeMoveRequest makeMoveRequest = serializer.fromJson(req.body(), MakeMoveRequest.class);
        makeMoveRequest.setAuthorization(req.headers("Authorization"));
        MakeMoveResult makeMoveResult = new MakeMoveService().makeMove(makeMoveRequest);
        res.status(makeMoveResult.getStatusCode());
        return serializer.toJson(makeMoveResult);
    }
}
