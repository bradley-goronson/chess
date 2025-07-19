package server.login;

import dataaccess.DataAccessException;
import model.UserData;
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
            System.out.println("Requested username:" + username);
            System.out.println("Given password:" + password);
            UserData targetUser = userDAO.getUser(username);
            System.out.println("target acquired");
            String targetPassword = targetUser.password();
            if (!password.equals(targetPassword)) {
                System.out.println("incorrect password");
                loginResult.setStatusCode(401);
                loginResult.setMessage("Error: unauthorized");
            } else {
                System.out.println("auth time");
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
