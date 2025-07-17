package server.login;

import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.UserData;
import service.Service;

public class LoginService extends Service {
    public LoginResult login(LoginRequest loginRequest) {
        LoginResult loginResult = new LoginResult();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        try {
            UserData targetUser = userDAO.getUser(username);
            if (!targetUser.password().equals(password)) {
                loginResult.setStatusCode(401);
                throw new UnauthorizedException("Error: unauthorized");
            }
            String authToken = authDAO.addAuth(username);
            loginResult.setStatusCode(200);
            loginResult.setUsername(username);
            loginResult.setAuthToken(authToken);
        } catch (DataAccessException e) {
            loginResult.setStatusCode(400);
            loginResult.setResultBody("Error: bad request");
        }
        return loginResult;
    }
}
