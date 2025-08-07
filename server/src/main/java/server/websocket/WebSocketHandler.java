package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.login.LoginService;
import service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        try {
            String username = new LoginService().getAuthDAO().getAuth(command.getAuthToken()).username();

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), username, session);
                case LEAVE -> leave(command.getGameID(), username);
                case RESIGN -> resign(command.getGameID(), username);
                case MAKE_MOVE -> makeMove(command.getGameID(), username, command.getMove());
            }
        } catch (UnauthorizedException | DataAccessException e) {
            session.getRemote().sendString(e.getMessage());
        }
    }

    private void connect(Integer gameID, String userName, Session session) throws IOException {
        connections.add(gameID, userName, session);
        String notifyMessage = String.format("%s has entered the game", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setNotificationText(notifyMessage);
        connections.broadcast(gameID, userName, notification);

        try {
            GameData currentGameState = new Service().getGameDAO().getGame(gameID);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(currentGameState);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
        } catch (GameNotFoundException | DataAccessException e) {
            ServerMessage loadGameError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            loadGameError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(loadGameError));
        }
    }

    private void leave(Integer gameID, String userName) throws IOException{
        connections.removeSession(gameID, userName);
        String message = String.format("%s has left the game", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setNotificationText(message);
        connections.broadcast(gameID, userName, notification);
    }

    private void resign(Integer gameID, String userName) throws IOException{
        connections.removeSession(gameID, userName);
        String message = String.format("%s has resigned from the game.", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setNotificationText(message);
        connections.broadcast(gameID, userName, notification);
    }

    private void makeMove(Integer gameID, String userName, ChessMove move) throws IOException {
        Session session = connections.getSession(gameID, userName);
        String message = String.format("%s moved", userName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notification.setNotificationText(message);
        connections.broadcast(gameID, userName, notification);

        try {
            GameData currentGameState = new Service().getGameDAO().getGame(gameID);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(currentGameState);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
        } catch (GameNotFoundException | DataAccessException e) {
            session.getRemote().sendString(e.getMessage());
        }
    }
}
