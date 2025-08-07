package server.move;

import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;
import service.Service;

public class MakeMoveService extends Service {
    public MakeMoveResult makeMove(MakeMoveRequest makeMoveRequest) {
        MakeMoveResult makeMoveResult = new MakeMoveResult();
        Integer gameID = makeMoveRequest.getGameID();
        ChessMove move = makeMoveRequest.getMove();
        try {
            System.out.print("You made it to the point where you're about to get the target gamedata! Provided move is" + move +"\n");
            GameData targetGameData = gameDAO.getGame(gameID);
            System.out.print("You made it to the point where you got the game! Target game is" + targetGameData +"\n");
            targetGameData.game().makeMove(move, null);
            System.out.print("You made it to the point where you just made the move! Updated game is" + targetGameData + "\n");
            makeMoveResult.setGameID(targetGameData.gameID());
            makeMoveResult.setWhiteUsername(targetGameData.whiteUsername());
            makeMoveResult.setBlackUsername(targetGameData.blackUsername());
            makeMoveResult.setGameName(targetGameData.gameName());
            makeMoveResult.setGame(targetGameData.game());
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        } catch (GameNotFoundException e) {
            makeMoveResult.setStatusCode(400);
            makeMoveResult.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            makeMoveResult.setStatusCode(500);
            makeMoveResult.setMessage("Error: lost connection when making a move");
        }
        return makeMoveResult;
    }
}
