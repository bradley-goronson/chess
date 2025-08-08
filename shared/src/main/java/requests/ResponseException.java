package requests;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    private final int status;

    public ResponseException(int status, String message) {
        super(message);
        this.status = status;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "statusCode", status));
    }

    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        int status = ((Double) map.get("statusCode")).intValue();
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int getStatusCode() {
        return status;
    }
}
