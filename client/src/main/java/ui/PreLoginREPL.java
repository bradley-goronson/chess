package ui;

import java.util.Scanner;

public class PreLoginREPL {
    private boolean loggedIn = false;
    private String authToken;

    public boolean repl() {
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
                case "register" -> loggedIn = login(requestArray);
                case "login" -> loggedIn = login(requestArray);
                case "quit" -> System.out.println("quitting application...");
                default -> System.out.println("error: invalid command given. use \"help\" for a list of available commands");
            }
        }
        System.out.println("you quit");
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
                        ": close chess application" +
                EscapeSequences.RESET_TEXT_COLOR);
    }

    private boolean login(String[] requestArray) {
        String username = requestArray[1];
        String password = requestArray[2];

        System.out.println("You're trying to login");
        System.out.println(username);
        System.out.println(password);
        //serverfacade
        //set auth
        return false;
    }

    private boolean register(String[] requestArray) {
        String username = requestArray[1];
        String password = requestArray[2];
        String email = requestArray[3];

        System.out.println("You're trying to register");
        System.out.println(username);
        System.out.println(password);
        System.out.println(email);
        //serverfacade
        //set auth
        return false;
    }

    public String getAuthToken() {
        return authToken;
    }
}
