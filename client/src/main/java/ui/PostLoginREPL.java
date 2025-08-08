package ui;

import model.GameData;
import requests.ResponseException;
import requests.ServerFacade;

import java.util.ArrayList;
import java.util.Scanner;

public class PostLoginREPL {
    String serverURL = "http://localhost:8080";
    boolean joinedGame = false;
    boolean loggedIn = true;
    boolean whitePerspective = true;
    boolean observer = false;
    GameData currentGameData = null;
    ArrayList<GameData> recentGameArray = new ArrayList<>();

    public boolean repl(String authToken) {
        String method;

        try {
            help();
            while (loggedIn && !joinedGame) {
                System.out.println("What would you like to do? ");
                Scanner scanner = new Scanner(System.in);
                String request = scanner.nextLine();
                String[] requestArray = request.split(" ");
                method = requestArray[0];
                recentGameArray = listGames(requestArray, authToken, false);

                switch (method) {
                    case "help" -> help();
                    case "logout" -> loggedIn = logout(requestArray, authToken);
                    case "create" -> createGame(requestArray, authToken);
                    case "list" -> recentGameArray = listGames(requestArray, authToken, true);
                    case "join" -> joinedGame = joinGame(requestArray, authToken);
                    case "observe" -> joinedGame = observeGame(requestArray);
                    default -> System.out.println(
                            EscapeSequences.SET_TEXT_COLOR_RED +
                                    "error: invalid command given - use \"help\" for a list of available commands and usages" +
                                    EscapeSequences.SET_TEXT_COLOR_WHITE);
                }
            }
        } catch (ResponseException e) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    e.getMessage() +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
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
        System.out.println("   note: the playerColor must be given in all caps, either WHITE or BLUE");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "observe" +
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
                            "Error: incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return true;
        }
        ServerFacade facade = new ServerFacade(serverURL);

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
        try {
            ServerFacade facade = new ServerFacade(serverURL);
            facade.createGame(requestArray[1], authToken);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_GREEN +
                "Successfully created game: " + requestArray[1] +
                EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private ArrayList<GameData> listGames(String[] requestArray, String authToken, boolean willDisplay) throws ResponseException {
        if (requestArray.length != 1 && willDisplay) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return recentGameArray;
        }
        ServerFacade facade = new ServerFacade(serverURL);
        recentGameArray = facade.listGames(authToken);
        if (willDisplay) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Successfully fetched games");
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_WHITE +
                            EscapeSequences.SET_TEXT_UNDERLINE +
                            "Active Chess Games" +
                            EscapeSequences.RESET_TEXT_UNDERLINE);
            if (recentGameArray.isEmpty()) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + "No active games" + EscapeSequences.SET_TEXT_COLOR_WHITE + "\n");
            }
            for (int i = 1; i <= recentGameArray.size(); i++) {
                GameData currentGame = recentGameArray.get(i - 1);
                System.out.println(
                        i + ") " + currentGame.gameName() + " - " +
                                "white player: " + currentGame.whiteUsername() +
                                ", black player: " + currentGame.blackUsername());
            }
            System.out.println("\n");
        }
        return recentGameArray;
    }

    private boolean joinGame(String[] requestArray, String authToken) throws ResponseException {
        if (requestArray.length != 3) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "Error: incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }

        if (!requestArray[2].equals("WHITE") && !requestArray[2].equals("BLACK")) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "Error: color must be either \"WHITE\" or \"BLACK\" in all capital letters" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }

        try {
            int requestedIndex = Integer.parseInt(requestArray[1]) - 1;
            currentGameData = recentGameArray.get(requestedIndex);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: bad request - must provide gameID before color and gameID must be a number");
        } catch (IndexOutOfBoundsException ex) {
            throw new ResponseException(400, "Error: invalid gameID");
        }

        ServerFacade facade = new ServerFacade(serverURL);
        facade.joinGame(currentGameData.gameID(), requestArray[2], authToken);

        whitePerspective = requestArray[2].equals("WHITE");
        return true;
    }

    private boolean observeGame(String[] requestArray) throws ResponseException {
        if (requestArray.length != 2) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "Error: incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }

        try {
            int requestedIndex = Integer.parseInt(requestArray[1]) - 1;
            currentGameData = recentGameArray.get(requestedIndex);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: bad request - must provide gameID before color and gameID must be a number");
        } catch (IndexOutOfBoundsException ex) {
            throw new ResponseException(400, "Error: invalid gameID");
        }

        whitePerspective = true;
        observer = true;
        return true;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public boolean getWhitePerspective() {
        return whitePerspective;
    }

    public GameData getCurrentGameData() {
        return currentGameData;
    }

    public boolean getObserver() {
        return observer;
    }
}
