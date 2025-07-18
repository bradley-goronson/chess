package server.createGame;

import server.Result;

public class CreateGameResult extends Result {
    Integer gameID;

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer newGameID) {
        gameID = newGameID;
    }
}
