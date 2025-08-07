package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getGameID(), command.getUserName(), session);
            case LEAVE -> leave(command.getGameID(), command.getUserName());
            case RESIGN -> resign(command.getGameID(), command.getUserName());
            case MAKE_MOVE -> makeMove(command.getGameID(), command.getUserName(), command.getMove());
        }
    }

    private void connect(Integer gameID, String userName, Session session) throws IOException {
        connections.add(gameID, userName, session);
        String message = String.format("%s has entered the game", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
    }

    private void leave(Integer gameID, String userName) throws IOException{
        connections.removeSession(gameID, userName);
        String message = String.format("%s has left the game", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
    }

    private void resign(Integer gameID, String userName) throws IOException{
        connections.removeSession(gameID, userName);
        String message = String.format("%s has resigned from the game.", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
    }

    private void makeMove(Integer gameID, String userName, ChessMove move) throws IOException {
        String message = String.format("%s moved", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message);
        connections.broadcast(gameID, userName, notification);
    }
}
