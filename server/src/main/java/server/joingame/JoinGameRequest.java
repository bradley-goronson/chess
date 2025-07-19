package server.joingame;

import server.Request;

public class JoinGameRequest extends Request {
    private String playerColor;
    private Integer gameID;

    public void setPlayColor(String newPlayerColor) {
        playerColor = newPlayerColor;
    }

    public String getPlayColor() {
        return playerColor;
    }

    public void setGameID(Integer newGameID) {
        gameID = newGameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
