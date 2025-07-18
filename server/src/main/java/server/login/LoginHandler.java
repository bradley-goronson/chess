package server.login;

import server.Handler;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler {
    public Object handle(Request req, Response res) {
        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = new LoginService().login(loginRequest);
        res.status(loginResult.getStatusCode());
        return serializer.toJson(loginResult);
    }
}
