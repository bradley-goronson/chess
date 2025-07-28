package ui;

import java.util.Scanner;

public class PostLoginREPL {
    boolean joinedGame = false;
    public boolean repl(String authToken) {
        System.out.println("What would you like to do? ");
        Scanner scanner = new Scanner(System.in);
        String request = scanner.nextLine();
        String[] requestArray = request.split(" ");
        String method = requestArray[0];

        while (!method.equals("quit")) {

        }
        return joinedGame;
    }

    private void help() {

    }

    private void logout() {

    }

    private boolean createGame() {
        return false;
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
