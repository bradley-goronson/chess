package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private ConcurrentHashMap<Integer, Collection<Connection>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String userName, Session session) {
        Connection connection = new Connection(userName, session);
        Collection<Connection> existingSessions = connections.get(gameID);
        if (existingSessions == null) {
            Collection<Connection> newConnectionList = new ArrayList<>();
            newConnectionList.add(connection);
            connections.put(gameID, newConnectionList);
        } else {
            existingSessions.add(connection);
            connections.put(gameID, existingSessions);
        }
    }

    public void removeSession(Integer gameID, String userName) {
        Collection<Connection> existingSessions = connections.get(gameID);
        for (Connection connection : existingSessions) {
            if (connection.userName.equals(userName)) {
                existingSessions.remove(connection);
                break;
            }
        }
    }

    public Session getSession(Integer gameID, String username) {
        Collection<Connection> currentGameSessions = connections.get(gameID);
        Session retrievedSession = null;
        if (currentGameSessions == null) {
            return retrievedSession;
        }
        for (Connection connection : currentGameSessions) {
            if (connection.userName.equals(username)) {
                retrievedSession = connection.session;
            }
        }
        return retrievedSession;
    }

    public void broadcast(Integer gameID, String excludeUserName, ServerMessage notification) throws IOException {
        Collection<Connection> currentGameSessions = connections.get(gameID);
        for (Connection connection : currentGameSessions) {
            if (connection.session.isOpen()) {
                if (!connection.userName.equals(excludeUserName)) {
                    connection.send(notification.toString());
                }
            } else {
                removeSession(gameID, connection.userName);
            }
        }
    }
}
