package websocket.handler;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String userName, Session session) {
        Connection connection = new Connection(userName, session);
        connections.put(gameID, connection);
    }

    public void remove(Integer gameID) {
        connections.remove(gameID);
    }
}
