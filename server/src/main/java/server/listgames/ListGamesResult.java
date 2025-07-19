package server.listgames;

import model.GameData;
import server.Result;

import java.util.ArrayList;

public class ListGamesResult extends Result {
    private ArrayList<GameData> games;

    public void setGamesArray(ArrayList<GameData> newGamesArray) {
        games = newGamesArray;
    }
}
