package server.logout;

import dataaccess.UnauthorizedException;
import service.Service;

public class LogoutService extends Service {
    public LogoutResult logout(LogoutRequest logoutRequest) {
        LogoutResult logoutResult = new LogoutResult();
        String authToken = logoutRequest.getAuthToken();

        try {
            authDAO.getAuth(authToken);
            authDAO.removeAuth(authToken);
            logoutResult.setStatusCode(200);
        } catch (UnauthorizedException e) {
            logoutResult.setStatusCode(401);
            logoutResult.setMessage(e.getMessage());
        }
        return logoutResult;
    }
}
