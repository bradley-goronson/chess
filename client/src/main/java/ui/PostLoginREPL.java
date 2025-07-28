package ui;

import model.GameData;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Scanner;

public class PostLoginREPL {
    boolean joinedGame = false;
    boolean loggedIn = true;
    ArrayList<GameData> recentGameArray = new ArrayList<>();

    public boolean repl(String authToken) {
        String method = "bacon cheeseburger";

        help();
        while (!method.equals("logout") && !joinedGame) {
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
        System.out.println(
                EscapeSequences.SET_TEXT_UNDERLINE + EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_WHITE +
                        "Available commands:" +
                        EscapeSequences.RESET_TEXT_UNDERLINE + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "help" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": display a list of available commands");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "logout" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": logout of user account");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "create" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": create a new chess game and name it with the provided name"
        );
        System.out.println("   usage: create <name>");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "list" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": display a list of all active chess games"
        );

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "join" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": join the indicated chess game playing as the indicated color"
        );
        System.out.println("   usage: join <gameID> <playerColor>");
        System.out.println("   note: the gameID is the number to the left of the chess game when using the \"list\" command");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "observer" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": observe the indicated chess game as a spectator"
        );
        System.out.println("   usage: observe <gameID>");
        System.out.println("   note: the gameID is the number to the left of the chess game when using the \"list\" command\n");
    }

    private void logout(String authToken) {
        ServerFacade facade = new ServerFacade();

        facade.logout();
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Successfully logged out" +
                        EscapeSequences.RESET_TEXT_COLOR);
        loggedIn = false;
    }

    private void createGame(String[] requestArray) {
        ServerFacade facade = new ServerFacade();

        facade.createGame();
    }

    private void listGames() {
        ServerFacade facade = new ServerFacade();

        recentGameArray = facade.listGames();
    }

    private boolean playGame() {
        ServerFacade facade = new ServerFacade();

        facade.joinGame();
        return false;
    }

    private boolean observeGame() {
        ServerFacade facade = new ServerFacade();
        facade.observeGame();
        return false;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }
}
