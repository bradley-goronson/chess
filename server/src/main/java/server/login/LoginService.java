package server.login;

import dataaccess.DataAccessException;
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
                System.out.println("Invalidated!");
                loginResult.setStatusCode(401);
                loginResult.setResultBody("Error: unauthorized");
            } else {
                System.out.println("Validated!");
                String authToken = authDAO.addAuth(username);
                loginResult.setStatusCode(200);
                loginResult.setUsername(username);
                loginResult.setAuthToken(authToken);
            }
        } catch (DataAccessException e) {
            System.out.println("Not enough info!");
            loginResult.setStatusCode(400);
            loginResult.setResultBody("Error: bad request");
        }
        System.out.println(loginResult.getStatusCode());
        return loginResult;
    }
}
