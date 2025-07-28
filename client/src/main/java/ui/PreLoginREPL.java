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

            if (method.equals("help")) {
                help();
            }
            if (method.equals("login")) {
                loggedIn = login(requestArray);
            }
            if (method.equals("register")) {
                loggedIn = register(requestArray);
            }
        }
        System.out.println("you quit");
        return loggedIn;
    }

    private void help() {
        System.out.println("Available commands:");
        System.out.println("help: display a list of available commands");
        System.out.println("register: create a new user account");
        System.out.println("   usage: <username> <password> <email>");
        System.out.println("login: login to an existing user account");
        System.out.println("   usage: <username> <password>");
        System.out.println("quit: close chess application");
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
