package server.register;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import model.UserData;
import service.Service;

public class RegisterService extends Service {
    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {
        RegisterResult registerResult = new RegisterResult();
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        try {
            userDAO.addUser(new UserData(username, password, email));
            String authToken = authDAO.addAuth(username);
            registerResult.setStatusCode(200);
            registerResult.setUsername(username);
            registerResult.setAuthToken(authToken);
        } catch (AlreadyTakenException e) {
            registerResult.setStatusCode(403);
            registerResult.setResultBody(e.getMessage());
        } catch (BadRequestException ex) {
            registerResult.setStatusCode(400);
            registerResult.setResultBody(ex.getMessage());
        }
        return registerResult;
    }
}
