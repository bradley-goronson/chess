package server.register;

import server.Result;

public class RegisterResult extends Result {
    RegisterResult(int statusCode) {
        setStatusCode(statusCode);
    }
}
