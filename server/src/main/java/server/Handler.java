package server;

import spark.Request;
import spark.Response;
import spark.Route;

public class Handler implements Route {
    public Object handle(Request req, Response res) {
        return "Hello, BYU!";
    }
}
