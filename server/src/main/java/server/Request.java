package server;

public class Request {
    private String authorization;
    private String username;
    private String password;
    private String email;
    private Integer gameID;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthToken() {
        return authorization;
    }

    public String getEmail() {
        return email;
    }
}
