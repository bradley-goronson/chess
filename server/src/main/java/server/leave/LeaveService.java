package server.leave;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;
import service.Service;

public class LeaveService extends Service {
    public LeaveResult leave(LeaveRequest leaveRequest) {
        LeaveResult leaveResult = new LeaveResult();
        String authToken = leaveRequest.getAuthToken();

        if (leaveRequest.getIsObserver()) {
            leaveResult.setStatusCode(200);
            return leaveResult;
        }

        try {
            authDAO.getAuth(authToken);
            GameData targetGame = gameDAO.getGame(leaveRequest.getGameID());
            GameData updatedGame = constructNewGame(targetGame, leaveRequest.getIsWhitePlayer());
            gameDAO.updateGame(leaveRequest.getGameID(), updatedGame);
            leaveResult.setStatusCode(200);
        } catch (UnauthorizedException e) {
            leaveResult.setStatusCode(401);
            leaveResult.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            leaveResult.setStatusCode(500);
            leaveResult.setMessage("Error: lost connection when leaving the game");
        }
        return leaveResult;
    }

    private GameData constructNewGame(GameData targetGame, boolean isWhitePlayer) {
        if (isWhitePlayer) {
            return new GameData(targetGame.gameID(), null, targetGame.blackUsername(), targetGame.gameName(), targetGame.game());
        }
        return new GameData(targetGame.gameID(), targetGame.whiteUsername(), null, targetGame.gameName(), targetGame.game());
    }
}
