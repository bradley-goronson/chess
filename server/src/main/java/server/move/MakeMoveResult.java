package server.move;

import chess.ChessGame;
import server.Result;

public class MakeMoveResult extends Result {
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame updatedGame;

    public void setGameID(Integer resultGameId) {
        gameID = resultGameId;
    }

    public void setWhiteUsername(String resultWhiteUsername) {
        whiteUsername = resultWhiteUsername;
    }

    public void setBlackUsername(String resultBlackUsername) {
        blackUsername = resultBlackUsername;
    }

    public void setGameName(String resultGameName) {
        gameName = resultGameName;
    }

    public void setGame(ChessGame game) {
        updatedGame = game;
    }
}