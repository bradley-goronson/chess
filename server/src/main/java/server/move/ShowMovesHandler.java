package server.move;

import server.Handler;
import spark.Request;
import spark.Response;

public class ShowMovesHandler extends Handler {
    public Object handle(Request req, Response res) {
        ShowMovesRequest showMovesRequest = serializer.fromJson(req.body(), ShowMovesRequest.class);
        showMovesRequest.setAuthorization(req.headers("Authorization"));
        ShowMovesResult showMovesResult = new ShowMovesService().showMoves(showMovesRequest);
        res.status(showMovesResult.getStatusCode());
        return serializer.toJson(showMovesResult);
    }
}
