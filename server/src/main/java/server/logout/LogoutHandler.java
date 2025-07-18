package server.logout;

import server.Handler;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    public Object handle(Request req, Response res) {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setAuthorization(req.headers("Authorization"));
        LogoutResult logoutResult = new LogoutService().logout(logoutRequest);
        res.status(logoutResult.getStatusCode());
        return serializer.toJson(logoutResult);
    }
}
