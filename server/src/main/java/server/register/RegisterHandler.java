package server.register;
import server.Handler;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler {
    public Object handle(Request req, Response res) {
        RegisterRequest registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);
        RegisterResult registerResult = new RegisterService().register(registerRequest);
        return serializer.toJson(registerResult);
    }
}
