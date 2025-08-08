package server.move;

import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;
import service.Service;

import java.util.Collection;

public class ShowMovesService extends Service {
    public ShowMovesResult showMoves(ShowMovesRequest showMovesRequest) {
        ShowMovesResult showMovesResult = new ShowMovesResult();
        Integer gameID = showMovesRequest.getGameID();
        ChessPosition startPosition = showMovesRequest.getStartPosition();
        try {
            GameData targetGameData = gameDAO.getGame(gameID);
            Collection<ChessMove> validMoves = targetGameData.game().validMoves(startPosition);
            showMovesResult.setStatusCode(200);
            showMovesResult.setMoves(validMoves);
        } catch (GameNotFoundException e) {
            showMovesResult.setStatusCode(400);
            showMovesResult.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            showMovesResult.setStatusCode(500);
            showMovesResult.setMessage("Error: lost connection when getting moves");
        }
        return showMovesResult;
    }
}
