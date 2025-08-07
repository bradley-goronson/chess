package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDataAccess;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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
            String username = new Service().getAuthDAO().getAuth(command.getAuthToken()).username();

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), username, session);
                case LEAVE -> leave(command.getGameID(), username);
                case RESIGN -> resign(command.getGameID(), username);
                case MAKE_MOVE -> makeMove(command.getGameID(), username, command.getMove());
            }
        } catch (UnauthorizedException | DataAccessException e) {
            ServerMessage authError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            authError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(authError.toString());
        }
    }

    private void connect(Integer gameID, String userName, Session session) throws IOException {
        try {
            GameData currentGameState = new Service().getGameDAO().getGame(gameID);
            connections.add(gameID, userName, session);
            String notifyMessage = String.format("%s has entered the game", userName);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(notifyMessage);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(currentGameState);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
            connections.broadcast(gameID, userName, notification);
        } catch (GameNotFoundException | DataAccessException e) {
            ServerMessage loadGameError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            loadGameError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(loadGameError));
        }
    }

    private void leave(Integer gameID, String userName) throws IOException{
        Session session = connections.getSession(gameID, userName);
        GameDataAccess gameDAO = new Service().getGameDAO();

        try {
            GameData currentGame = gameDAO.getGame(gameID);
            String blackUsername = currentGame.blackUsername();
            String whiteUsername = currentGame.whiteUsername();

            connections.removeSession(gameID, userName);

            if (userName.equals(currentGame.whiteUsername())) {
                whiteUsername = null;
            }
            if (userName.equals(currentGame.blackUsername())) {
                blackUsername = null;
            }

            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID, whiteUsername, blackUsername, currentGame.gameName(), currentGame.game())
            );

            String message = String.format("%s has left the game.", userName);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(gameID, userName, notification);
            GameData updatedGameData = gameDAO.getGame(gameID);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(updatedGameData);
            connections.broadcast(gameID, null, loadGameMessage);
        } catch (GameNotFoundException | DataAccessException e) {
            ServerMessage leaveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            leaveError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(leaveError));
        }
    }

    private void resign(Integer gameID, String userName) throws IOException {
        Session session = connections.getSession(gameID, userName);
        GameDataAccess gameDAO = new Service().getGameDAO();

        try {
            GameData currentGame = gameDAO.getGame(gameID);
            String blackUsername = currentGame.blackUsername();
            String whiteUsername = currentGame.whiteUsername();

            connections.removeSession(gameID, userName);

            if (userName.equals(currentGame.whiteUsername())) {
                whiteUsername = null;
            }
            if (userName.equals(currentGame.blackUsername())) {
                blackUsername = null;
            }

            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID, whiteUsername, blackUsername, currentGame.gameName(), currentGame.game())
            );

            String message = String.format("%s has resigned from the game.", userName);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(gameID, userName, notification);
            GameData updatedGameData = gameDAO.getGame(gameID);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(updatedGameData);
            connections.broadcast(gameID, null, loadGameMessage);
        } catch (GameNotFoundException | DataAccessException e) {
            ServerMessage leaveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            leaveError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(leaveError));
        }
    }

    private void makeMove(Integer gameID, String userName, ChessMove move) throws IOException {
        Session session = connections.getSession(gameID, userName);
        GameDataAccess gameDAO = new Service().getGameDAO();

        try {
            GameData currentGame = gameDAO.getGame(gameID);
            ChessGame.TeamColor playerColor;
            if (userName.equals(currentGame.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (userName.equals(currentGame.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                playerColor = null;
            }
            ChessGame updatedGame = gameDAO.getGame(gameID).game().makeMove(move, playerColor);
            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID, currentGame.whiteUsername(), currentGame.blackUsername(), currentGame.gameName(), updatedGame)
            );
            GameData updatedGameData = gameDAO.getGame(gameID);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(updatedGameData);
            connections.broadcast(gameID, null, loadGameMessage);
            String message = String.format("%s moved", userName);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(gameID, userName, notification);

            evaluateWinConditions(updatedGameData);

        } catch (InvalidMoveException | GameNotFoundException | DataAccessException e) {
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            moveError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(moveError));
        }
    }

    private void evaluateWinConditions(GameData updatedGameData) throws IOException {
        ChessGame updatedGame = updatedGameData.game();
        if (updatedGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            updatedGame.setGameOver(true);
            String message = String.format("The game is over! %s has won.", updatedGameData.blackUsername());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(updatedGameData.gameID(), null, notification);
        }
        if (updatedGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            updatedGame.setGameOver(true);
            String message = String.format("The game is over! %s has won.", updatedGameData.whiteUsername());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(updatedGameData.gameID(), null, notification);
        }
        if (updatedGame.isInStalemate(ChessGame.TeamColor.WHITE) || updatedGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            updatedGame.setGameOver(true);
            String message = "The game is over! It was a stalemate.";
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(updatedGameData.gameID(), null, notification);
        }
    }
}
