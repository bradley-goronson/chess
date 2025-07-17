package server.clear;

import server.Result;

public class ClearResult extends Result {
    ClearResult(int statusCode) {
        setStatusCode(statusCode);
        setResultBody(null);
    }
}
