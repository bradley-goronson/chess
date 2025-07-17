package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class Handler implements Route {
    public Gson serializer = new Gson();
    public Object handle(Request req, Response res) {
        return "Hello, BYU!";
    }
}
