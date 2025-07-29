package ui;

import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Scanner;

public class PostLoginREPL {
    boolean joinedGame = false;
    boolean loggedIn = true;
    ArrayList<GameData> recentGameArray = new ArrayList<>();

    public boolean repl(String authToken) throws ResponseException {
        String method;

        help();
        while (loggedIn && !joinedGame) {
            System.out.println("What would you like to do? ");
            Scanner scanner = new Scanner(System.in);
            String request = scanner.nextLine();
            String[] requestArray = request.split(" ");
            method = requestArray[0];

            switch (method) {
                case "help" -> help();
                case "logout" -> loggedIn = logout(requestArray, authToken);
                case "createGame" -> createGame(requestArray, authToken);
                case "list" -> listGames(requestArray);
                case "join" -> joinedGame = joinGame(requestArray, authToken);
                case "observe" -> joinedGame = observeGame(requestArray);
                default -> System.out.println(
                        EscapeSequences.SET_TEXT_COLOR_RED +
                                "error: invalid command given - use \"help\" for a list of available commands and usages" +
                                EscapeSequences.SET_TEXT_COLOR_WHITE);
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

    private boolean logout(String[] requestArray, String authToken) throws ResponseException {
        if (requestArray.length != 1) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return true;
        }
        ServerFacade facade = new ServerFacade("blank");

        facade.logout(authToken);
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Successfully logged out" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
        return false;
    }

    private void createGame(String[] requestArray, String authToken) throws ResponseException {
        if (requestArray.length != 2) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return;
        }
        ServerFacade facade = new ServerFacade("blank");
        facade.createGame(requestArray[1], authToken);
    }

    private void listGames(String[] requestArray) {
        if (requestArray.length != 1) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return;
        }
        ServerFacade facade = new ServerFacade("BLAKN");

        recentGameArray = facade.listGames();
    }

    private boolean joinGame(String[] requestArray, String authToken) {
        if (requestArray.length != 3) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
        ServerFacade facade = new ServerFacade("BLANK");

        facade.joinGame();
        return true;
    }

    private boolean observeGame(String[] requestArray) {
        if (requestArray.length != 2) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
        ServerFacade facade = new ServerFacade("BLANK");
        facade.observeGame();
        return true;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }
}
