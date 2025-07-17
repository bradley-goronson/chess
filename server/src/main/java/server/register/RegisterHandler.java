package server.register;
import server.Handler;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler {
    public Object handle(Request req, Response res) {
        return "Hello!";
    }
}
