package server.register;

import server.Request;

public class RegisterRequest extends Request {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String newEmail) {
        email = newEmail;
    }
}
