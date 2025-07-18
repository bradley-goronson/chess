package server.createGame;

import server.Request;

public class CreateGameRequest extends Request {
    private String gameName;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String newGameName) {
        gameName = newGameName;
    }
}
