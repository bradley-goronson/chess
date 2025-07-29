package ui;

import server.ResponseException;
import server.ServerFacade;

import java.util.Scanner;

public class PreLoginREPL {
    private boolean loggedIn = false;
    private boolean quit = false;
    private String authToken = "";
    private String serverURL = "http://localhost:8080";

    public boolean repl() throws ResponseException {
        String method = "mint chocolate chip ice cream";

        help();
        while (!method.equals("quit") && !loggedIn) {
            System.out.println("What would you like to do? ");
            Scanner scanner = new Scanner(System.in);
            String request = scanner.nextLine();
            String[] requestArray = request.split(" ");
            method = requestArray[0];

            switch (method) {
                case "help" -> help();
                case "register" -> loggedIn = register(requestArray);
                case "login" -> loggedIn = login(requestArray);
                case "quit" -> quit = quit(requestArray);
                default -> System.out.println(
                        EscapeSequences.SET_TEXT_COLOR_RED +
                        "error: invalid command given - use \"help\" for a list of available commands and usages" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
            }

            if (quit) {
                System.out.println(
                        "Quitting application..." +
                        EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Done" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
            }

            if (loggedIn) {
                System.out.println(
                        EscapeSequences.SET_TEXT_COLOR_GREEN +
                        "Successfully logged in as: " + requestArray[1] +
                        EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
        }
        return loggedIn;
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
                "register" +
                EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": create a new user account");
        System.out.println("   usage: register <username> <password> <email>");

        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "login" +
                EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": login to an existing user account"
        );
        System.out.println("   usage: login <username> <password>");
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.SET_TEXT_BOLD +
                        "quit" +
                EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_TEXT_BOLD_FAINT +
                        ": close chess application\n" +
                EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private boolean login(String[] requestArray) throws ResponseException {
        if (requestArray.length != 3) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
        ServerFacade facade = new ServerFacade(serverURL);
        authToken = facade.login(requestArray[1], requestArray[2]);
        return !authToken.isEmpty();
    }

    private boolean register(String[] requestArray) throws ResponseException {
        if (requestArray.length != 4) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
        ServerFacade facade = new ServerFacade(serverURL);
        authToken = facade.register(requestArray[1], requestArray[2], requestArray[3]);
        if (!authToken.isEmpty()) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_GREEN +
                    "Successfully registered account!" +
                    EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
        return !authToken.isEmpty();
    }

    private boolean quit(String[] requestArray) {
        if (requestArray.length != 1) {
            System.out.println(
                    EscapeSequences.SET_TEXT_COLOR_RED +
                            "error incorrect number of arguments given - use \"help\" for a list of available commands and usages" +
                            EscapeSequences.SET_TEXT_COLOR_WHITE);
            return false;
        }
        return true;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean getQuit() {
        return quit;
    }
}
