package server.login;

import server.Handler;
import server.register.RegisterRequest;
import server.register.RegisterResult;
import server.register.RegisterService;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler {
    public Object handle(Request req, Response res) {
        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = new LoginService().login(loginRequest);
        return serializer.toJson(loginResult);
    }
}
