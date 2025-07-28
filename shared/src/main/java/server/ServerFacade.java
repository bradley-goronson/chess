package server;

import model.GameData;

import java.util.ArrayList;

public class ServerFacade {
    public String register(String username, String password, String email) {
        String authToken = "null";


        return authToken;
    }

    public String login(String username, String password) {
        String authToken = "null";


        return authToken;
    }

    public void logout() {

    }

    public ArrayList<GameData> listGames() {
        ArrayList<GameData> gamesArray = new ArrayList<>();

        return gamesArray;
    }

    public void createGame() {

    }

    public void joinGame() {

    }

    public void observeGame() {

    }
}
