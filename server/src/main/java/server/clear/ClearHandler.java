package server.clear;
import server.Handler;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler {
    public Object handle(Request req, Response res) {
        ClearResult clearResult = new ClearService().clear();
        res.status(clearResult.getStatusCode());
        return serializer.toJson(clearResult);
    }
}