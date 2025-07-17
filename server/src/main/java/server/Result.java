package server;

public class Result {
    private int statusCode;
    private String resultBody;
    private String username;
    private String authToken;

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public void setAuthToken(String newAuthToken) {
        authToken = newAuthToken;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResultBody() {
        return resultBody;
    }

    public void setStatusCode(int code) {
        statusCode = code;
    }

    public void setResultBody(String body) {
        resultBody = body;
    }
}
