package server;

public class Result {
    private int statusCode;
    private String resultBody;

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
