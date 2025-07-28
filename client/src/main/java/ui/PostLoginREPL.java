package ui;

import model.GameData;

import java.util.ArrayList;
import java.util.Scanner;

public class PostLoginREPL {
    boolean joinedGame = false;

    public boolean repl(String authToken) {
        String method = "bacon cheeseburger";
        ArrayList<GameData> recentGameArray = new ArrayList<>();

        while (!method.equals("quit") && !joinedGame) {
            System.out.println("What would you like to do ROUND TWO? ");
            Scanner scanner = new Scanner(System.in);
            String request = scanner.nextLine();
            String[] requestArray = request.split(" ");
            method = requestArray[0];

            switch (method) {
                case "help" -> help();
                case "logout" -> logout(authToken);
                case "createGame" -> createGame(requestArray);
                case "list" -> listGames();
                case "join" -> joinedGame = playGame();
                case "observer" -> joinedGame = observeGame();
            }
        }
        return joinedGame;
    }

    private void help() {

    }

    private void logout(String authToken) {

    }

    private void createGame(String[] requestArray) {

    }

    private void listGames() {

    }

    private boolean playGame() {
        return false;
    }

    private boolean observeGame() {
        return false;
    }
}
