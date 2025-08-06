package server;

import dataaccess.exceptions.DataAccessException;
import dataaccess.DatabaseManager;
import server.clear.ClearHandler;
import server.creategame.CreateGameHandler;
import server.joingame.JoinGameHandler;
import server.leave.LeaveHandler;
import server.listgames.ListGamesHandler;
import server.login.LoginHandler;
import server.logout.LogoutHandler;
import server.register.RegisterHandler;
import spark.*;
import websocket.handler.WebSocketHandler;

public class Server {
    private final static WebSocketHandler webSocketHandler = new WebSocketHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void createRoutes() {
        Spark.webSocket("/ws", webSocketHandler);
        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.get("/game", new ListGamesHandler());
        Spark.put("/game", new JoinGameHandler());
        Spark.put("/leave", new LeaveHandler());
    }
}
