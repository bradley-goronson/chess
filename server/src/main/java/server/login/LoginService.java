package server.login;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.Service;

public class LoginService extends Service {
    public LoginResult login(LoginRequest loginRequest) {
        LoginResult loginResult = new LoginResult();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            loginResult.setStatusCode(400);
            loginResult.setMessage("Error: bad request");
            return loginResult;
        }

        try {
            UserData targetUser = userDAO.getUser(username);
            String targetPassword = targetUser.password();
            if (!BCrypt.checkpw(password, targetPassword)) {
                loginResult.setStatusCode(401);
                loginResult.setMessage("Error: unauthorized");
            } else {
                String authToken = authDAO.addAuth(username);
                loginResult.setStatusCode(200);
                loginResult.setUsername(username);
                loginResult.setAuthToken(authToken);
            }
        } catch (DataAccessException e) {
            loginResult.setStatusCode(401);
            loginResult.setMessage("Error: unauthorized");
        }
        return loginResult;
    }
}
