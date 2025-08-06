package websocket.handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketHandler {
    private ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getGameID(), command.getUserName(), session);
            case LEAVE -> leave();
            case RESIGN -> resign();
            case MAKE_MOVE -> makeMove();
        }
    }

    private void connect(Integer gameID, String userName, Session session) {
        connections.add(gameID, userName, session);
    }

    private void leave() {

    }

    private void resign() {

    }

    private void makeMove() {

    }
}
