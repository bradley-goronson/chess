package ui;

import java.util.Scanner;

public class PreLoginREPL {
    private boolean loggedIn = false;
    private String authToken = null;

    public boolean repl() {
        String method = "mint chocolate chip ice cream";
        while (!method.equals("quit") || !loggedIn) {
            System.out.println("What would you like to do? ");
            Scanner scanner = new Scanner(System.in);
            String request = scanner.nextLine();
            String[] requestArray = request.split(" ");
            method = requestArray[0];

            if (method.equals("help")) {
                help();
            }
            if (method.equals("login")) {
                loggedIn = login();
            }
            if (method.equals("register")) {
                loggedIn = register();
            }
        }
        return loggedIn;
    }

    private void help() {

    }

    private boolean login() {
        return false;
    }

    private boolean register() {

        return false;
    }

    public String getAuthToken() {
        return authToken;
    }
}
