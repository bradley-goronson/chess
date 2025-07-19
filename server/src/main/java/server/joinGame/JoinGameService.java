package server.joinGame;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.GameNotFoundException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import service.Service;

public class JoinGameService extends Service {
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        JoinGameResult joinGameResult = new JoinGameResult();
        String authToken = joinGameRequest.getAuthToken();
        String playerColor = joinGameRequest.getPlayColor();
        Integer gameID = joinGameRequest.getGameID();

        if (authToken == null || playerColor == null || gameID == null || !(playerColor.equals("WHITE") || playerColor.equals("BLACK"))) {
            joinGameResult.setStatusCode(400);
            joinGameResult.setMessage("Error: bad request");
            return joinGameResult;
        }

        try {
            AuthData validatedAuth = authDAO.getAuth(authToken);
            GameData targetGame = gameDAO.getGame(gameID);
            colorAvailable(playerColor, targetGame);
            String requestorUsername = validatedAuth.username();
            GameData updatedGame = constructNewGame(playerColor, targetGame, requestorUsername);
            gameDAO.updateGame(gameID, updatedGame);
            joinGameResult.setStatusCode(200);
        } catch (UnauthorizedException e) {
            joinGameResult.setStatusCode(401);
            joinGameResult.setMessage(e.getMessage());
        } catch (GameNotFoundException ex) {
            joinGameResult.setStatusCode(400);
            joinGameResult.setMessage(ex.getMessage());
        } catch (AlreadyTakenException exception) {
            joinGameResult.setStatusCode(403);
            joinGameResult.setMessage(exception.getMessage());
        }

        return joinGameResult;
    }

    private void colorAvailable(String playerColor, GameData game) throws AlreadyTakenException {
        if (playerColor.equals("WHITE") && game.whiteUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        if (playerColor.equals("BLACK") && game.blackUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
    }

    private GameData constructNewGame(String playerColor, GameData game, String username) {
        Integer gameID = game.gameID();
        String gameName = game.gameName();
        ChessGame chessGame = game.game();
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (playerColor.equals("WHITE")) {
            whiteUsername = username;
        }
        if (playerColor.equals("BLACK")) {
            blackUsername = username;
        }
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }
}
