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
            System.out.println("Good job! You got a username! It is: " + username);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), username, session);
                case LEAVE -> leave(command.getGameID(), username);
                case RESIGN -> resign(command.getGameID(), username);
                case MAKE_MOVE -> makeMove(command.getGameID(), username, command.getMove(), command.getFirstPosition(), command.getLastPosition());
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

    private void leave(Integer gameID, String userName) throws IOException {
        System.out.println("We are trying to leave! the gameID is:" + gameID + "\n");
        if (gameID != null) {
            Session session = connections.getSession(gameID, userName);
            System.out.println("You made a session! Good job! It is: " + session);
            if (session != null) {
                GameDataAccess gameDAO = new Service().getGameDAO();

                try {
                    GameData currentGame = gameDAO.getGame(gameID);
                    String blackUsername = currentGame.blackUsername();
                    String whiteUsername = currentGame.whiteUsername();

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
                    connections.removeSession(gameID, userName);
                } catch (GameNotFoundException | DataAccessException e) {
                    ServerMessage leaveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    leaveError.setErrorMessage(e.getMessage());
                    session.getRemote().sendString(new Gson().toJson(leaveError));
                }
            }
        }
    }

    private void resign(Integer gameID, String userName) throws IOException {
        Session session = connections.getSession(gameID, userName);
        if (session == null) {
            return;
        }
        GameDataAccess gameDAO = new Service().getGameDAO();

        try {
            GameData currentGame = gameDAO.getGame(gameID);
            String blackUsername = currentGame.blackUsername();
            String whiteUsername = currentGame.whiteUsername();

            if (currentGame.game().isGameOver()) {
                ServerMessage resignError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                resignError.setErrorMessage("Error: You can't resign, the game is already over. Leave instead.");
                session.getRemote().sendString(new Gson().toJson(resignError));
                return;
            }

            if (isObserver(currentGame, userName)) {
                ServerMessage resignError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                resignError.setErrorMessage("Error: Observers can't resign. Leave the game instead.");
                session.getRemote().sendString(new Gson().toJson(resignError));
                return;
            }

            String otherUsername;
            if (userName.equals(blackUsername)) {
                otherUsername = whiteUsername;
            } else {
                otherUsername = blackUsername;
            }

            if (userName.equals(currentGame.whiteUsername())) {
                whiteUsername = null;
            }
            if (userName.equals(currentGame.blackUsername())) {
                blackUsername = null;
            }

            currentGame.game().setGameOver(true);
            gameDAO.updateGame(
                    gameID,
                    new GameData(gameID, whiteUsername, blackUsername, currentGame.gameName(), currentGame.game())
            );

            String message = String.format("%s has resigned from the game. %s has won.", userName, otherUsername);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(gameID, null, notification);
            //connections.removeSession(gameID, userName);
        } catch (GameNotFoundException | DataAccessException e) {
            ServerMessage resignError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            resignError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(resignError));
        }
    }

    private void makeMove(Integer gameID, String userName, ChessMove move, String start, String end) throws IOException {
        Session session = connections.getSession(gameID, userName);
        if (session == null) {
            return;
        }
        GameDataAccess gameDAO = new Service().getGameDAO();

        try {
            GameData currentGame = gameDAO.getGame(gameID);
            if (isObserver(currentGame, userName)) {
                ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                moveError.setErrorMessage("Error: Observers can't make moves.");
                session.getRemote().sendString(new Gson().toJson(moveError));
                return;
            }

            ChessGame.TeamColor playerColor;
            if (userName.equals(currentGame.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            if (currentGame.game().getBoard().getPiece(move.getStartPosition()).pieceColor != playerColor) {
                ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                moveError.setErrorMessage("Error: You can't move an opponents piece!");
                session.getRemote().sendString(new Gson().toJson(moveError));
                return;
            }

            ChessGame updatedGame = gameDAO.getGame(gameID).game().makeMove(move);
            String whiteUsername = currentGame.whiteUsername();
            String blackUsername = currentGame.blackUsername();
            GameData updatedGameData = new GameData(gameID, whiteUsername, blackUsername, currentGame.gameName(), updatedGame);
            if (winCondition(updatedGameData)) {
                updatedGameData.game().setGameOver(true);
            }
            gameDAO.updateGame(gameID, updatedGameData);
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGameMessage.setGame(updatedGameData);
            connections.broadcast(gameID, null, loadGameMessage);
            String message = String.format("%s moved from %s to %s", userName, start, end);
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(gameID, userName, notification);

        } catch (InvalidMoveException | GameNotFoundException | DataAccessException e) {
            ServerMessage moveError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            moveError.setErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(moveError));
        }
    }

    private boolean winCondition(GameData updatedGameData) throws IOException {
        ChessGame updatedGame = updatedGameData.game();
        if (updatedGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            updatedGame.setGameOver(true);
            String message = String.format("The game is over! %s has won.", updatedGameData.blackUsername());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(updatedGameData.gameID(), null, notification);
            return true;
        } else if (updatedGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            updatedGame.setGameOver(true);
            String message = String.format("The game is over! %s has won.", updatedGameData.whiteUsername());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(updatedGameData.gameID(), null, notification);
            return true;
        } else if (updatedGame.isInStalemate(ChessGame.TeamColor.WHITE) || updatedGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            updatedGame.setGameOver(true);
            String message = "The game is over! It was a stalemate.";
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.setNotificationText(message);
            connections.broadcast(updatedGameData.gameID(), null, notification);
            return true;
        } else {
            return false;
        }
    }

    private boolean isObserver(GameData gameData, String username) {
        if (username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername())) {
            return false;
        }
        return true;
    }
}
