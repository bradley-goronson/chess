package server;

public class Result {
    private int statusCode;
    private String resultBody;
    private String username;
    private String authToken;

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setAuthToken(String newAuthToken) {
        authToken = newAuthToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int code) {
        statusCode = code;
    }


    public String getResultBody() {
        return resultBody;
    }

    public void setResultBody(String body) {
        resultBody = body;
    }
}
