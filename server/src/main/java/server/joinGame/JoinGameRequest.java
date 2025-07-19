package server.joinGame;

import server.Request;

public class JoinGameRequest extends Request {
    private String playColor;
    private Integer gameID;

    public void setPlayColor(String newPlayerColor) {
        playColor = newPlayerColor;
    }

    public String getPlayColor() {
        return playColor;
    }

    public void setGameID(Integer newGameID) {
        gameID = newGameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
