package ui;

import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class PostLoginREPL {
    boolean joinedGame = false;
    boolean loggedIn = true;
    String serverURL = "http://localhost:8080";
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

                switch (method) {
                    case "help" -> help();
                    case "logout" -> loggedIn = logout(requestArray, authToken);
                    case "create" -> createGame(requestArray, authToken);
                    case "list" -> recentGameArray = listGames(requestArray, authToken);
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
                    "response error" +
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
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
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

    private ArrayList<GameData> listGames(String[] requestArray, String authToken) throws ResponseException {
        if (requestArray.length != 1) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return recentGameArray;
        }
        ServerFacade facade = new ServerFacade(serverURL);

        recentGameArray = facade.listGames(authToken);
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
        return recentGameArray;
    }

    private boolean joinGame(String[] requestArray, String authToken) throws ResponseException {
        if (requestArray.length != 3) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error: incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }

        if (!requestArray[2].equals("WHITE") && !requestArray[2].equals("BLACK")) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error: invalid color given - must provide either \"WHITE\" or \"BLACK\" in all capital letters" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }

        try {
            int requestedIndex = Integer.parseInt(requestArray[1]) - 1;
            recentGameArray.get(requestedIndex);
        } catch (NumberFormatException ex) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error: invalid gameID" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "invalid gameID" + EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }

        ServerFacade facade = new ServerFacade(serverURL);
        facade.joinGame(requestArray[1], requestArray[2], authToken);

        printBoard(requestArray[2].equals("WHITE"));
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
        int requestedIndex = Integer.parseInt(requestArray[1]) - 1;
        try {
            recentGameArray.get(requestedIndex);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "invalid gameID" + EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
        printBoard(true);
        return true;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    private void printBoard(boolean whitePerspective) {
        PrintStream output = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String wp = EscapeSequences.SET_TEXT_COLOR_WHITE + "P";
        String wr = EscapeSequences.SET_TEXT_COLOR_WHITE + "R";
        String wn = EscapeSequences.SET_TEXT_COLOR_WHITE + "N";
        String wb = EscapeSequences.SET_TEXT_COLOR_WHITE + "B";
        String wq = EscapeSequences.SET_TEXT_COLOR_WHITE + "Q";
        String wk = EscapeSequences.SET_TEXT_COLOR_WHITE + "K";

        String bp = EscapeSequences.SET_TEXT_COLOR_BLACK + "P";
        String br = EscapeSequences.SET_TEXT_COLOR_BLACK + "R";
        String bn = EscapeSequences.SET_TEXT_COLOR_BLACK + "N";
        String bb = EscapeSequences.SET_TEXT_COLOR_BLACK + "B";
        String bq = EscapeSequences.SET_TEXT_COLOR_BLACK + "Q";
        String bk = EscapeSequences.SET_TEXT_COLOR_BLACK + "K";

        String[][] rowArray = {
                {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "},
                {"8", br, bn, bb, bq, bk, bb, bn, br, "8"},
                {"7", bp, bp, bp, bp, bp, bp, bp, bp, "7"},
                {"6", " ", " ", " ", " ", " ", " ", " ", " ", "6"},
                {"5", " ", " ", " ", " ", " ", " ", " ", " ", "5"},
                {"4", " ", " ", " ", " ", " ", " ", " ", " ", "4"},
                {"3", " ", " ", " ", " ", " ", " ", " ", " ", "3"},
                {"2", wp, wp, wp, wp, wp, wp, wp, wp, "2"},
                {"1", wr, wn, wb, wq, wk, wb, wn, wr, "1"},
                {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "}
        };

        if (whitePerspective) {
            printWhiteBoard(rowArray, output);
        } else {
            printBlackBoard(rowArray, output);
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "\n");
    }

    private void printWhiteBoard(String[][] rowArray, PrintStream output) {
        int rowNumber = 1;
        for (String[] row : rowArray) {
            printRow(output, row, rowNumber);
            rowNumber++;
        }
    }

    private void printBlackBoard(String[][] rowArray, PrintStream output) {
        int rowNumber = 1;
        for (int i = 10; i > 0; i--) {
            String[] row = rowArray[i - 1];
            if (rowNumber == 1 || rowNumber == 10) {
                row = new String[] {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
            }
            printRow(output, row, rowNumber);
            rowNumber++;
        }
    }

    private void printRow(PrintStream output, String[] row, int rowNumber) {
        output.print(EscapeSequences.SET_BG_COLOR_MAGENTA);
        output.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA);

        if (rowNumber == 1 || rowNumber == 10) {
            printEdge(output, row);
        } else if (rowNumber % 2 == 0) {
            printEvenRow(output, row);
        } else {
            printOddRow(output, row);
        }
        output.print(EscapeSequences.SET_BG_COLOR_BLACK + "\n");
    }

    private void printEdge(PrintStream output, String[] row) {
        for (String letter : row) {
            output.print(" ");
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            output.print(letter);
            output.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA);
            output.print(" ");
        }
    }

    private void printEvenRow(PrintStream output, String[] row) {
        int tileNumber = 1;
        String backgroundColor;
        String textColor;

        for (String letter : row) {
            if (tileNumber == 1 || tileNumber == 10) {
                backgroundColor = EscapeSequences.SET_BG_COLOR_MAGENTA;
                textColor = EscapeSequences.SET_TEXT_COLOR_YELLOW;
            } else if (tileNumber % 2 == 0) {
                backgroundColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                if (!letter.equals(EscapeSequences.EMPTY)) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
                }
            } else {
                backgroundColor = EscapeSequences.SET_BG_COLOR_BLUE;
                if (!letter.equals(EscapeSequences.EMPTY)) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
                }
            }

            output.print(backgroundColor);
            output.print(" ");
            output.print(textColor);
            output.print(letter);
            output.print(" ");
            tileNumber++;
        }
    }

    private void printOddRow(PrintStream output, String[] row) {
        int tileNumber = 1;
        String backgroundColor;
        String textColor;

        for (String letter : row) {
            if (tileNumber == 1 || tileNumber == 10) {
                backgroundColor = EscapeSequences.SET_BG_COLOR_MAGENTA;
                textColor = EscapeSequences.SET_TEXT_COLOR_YELLOW;
            } else if (tileNumber % 2 == 0) {
                backgroundColor = EscapeSequences.SET_BG_COLOR_BLUE;
                if (!letter.equals(EscapeSequences.EMPTY)) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
                }
            } else {
                backgroundColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                if (!letter.equals(EscapeSequences.EMPTY)) {
                    textColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
                } else {
                    textColor = EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
                }
            }

            output.print(backgroundColor);
            output.print(" ");
            output.print(textColor);
            output.print(letter);
            output.print(" ");
            tileNumber++;
        }
    }
}
