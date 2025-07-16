package server.clear;
import server.Handler;
import server.Response;
import spark.Request;

public class ClearHandler extends Handler {
    public Object handle(Request req, Response res) {
        new ClearService().clear();
        return "We tried!";
    }
}
