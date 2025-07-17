package server;

public class Request {
    private String authorization;
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthToken() {
        return authorization;
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public void setAuthorization(String authToken) {
        authorization = authToken;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }
}
