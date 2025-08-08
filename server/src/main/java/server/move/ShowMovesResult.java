package server.move;

import chess.ChessMove;
import server.Result;

import java.util.Collection;

public class ShowMovesResult extends Result {
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private Collection<ChessMove> validMoves;

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

    public void setMoves(Collection<ChessMove> moves) {
        validMoves = moves;
    }
}