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
import server.move.MakeMoveHandler;
import server.register.RegisterHandler;
import spark.*;
import server.websocket.WebSocketHandler;

public class Server {
    private final static WebSocketHandler WEB_SOCKET_HANDLER = new WebSocketHandler();

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
        Spark.webSocket("/ws", WEB_SOCKET_HANDLER);
        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.get("/game", new ListGamesHandler());
        Spark.put("/game", new JoinGameHandler());
        Spark.put("/leave", new LeaveHandler());
        Spark.put("/move", new MakeMoveHandler());
    }
}
