package server.leave;

import server.Handler;
import spark.Request;
import spark.Response;

public class LeaveHandler extends Handler {
    public Object handle(Request req, Response res) {
        LeaveRequest leaveRequest = serializer.fromJson(req.body(), LeaveRequest.class);
        leaveRequest.setAuthorization(req.headers("Authorization"));
        LeaveResult leaveResult = new LeaveService().leave(leaveRequest);
        res.status(leaveResult.getStatusCode());
        return serializer.toJson(leaveResult);
    }
}
