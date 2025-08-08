import requests.ResponseException;
import ui.EscapeSequences;
import ui.GamePlayREPL;
import ui.PostLoginREPL;
import ui.PreLoginREPL;

public class Main {
    public static void main(String[] args) {
        System.out.println(
                EscapeSequences.SET_TEXT_COLOR_WHITE +
                EscapeSequences.SET_TEXT_BOLD +
                "♖♘♗♔ BYU CS 240 Chess Client ♕♗♘♖" +
                EscapeSequences.SET_TEXT_COLOR_WHITE);

        boolean quit = false;
        while (!quit) {
            String authToken;
            PreLoginREPL preLogin = new PreLoginREPL();
            boolean loggedIn = preLogin.repl();
            quit = preLogin.getQuit();

            while (loggedIn) {
                authToken = preLogin.getAuthToken();
                PostLoginREPL postLogin = new PostLoginREPL();
                boolean joinedGame = postLogin.repl(authToken);
                if (joinedGame) {
                    try {
                        GamePlayREPL gamePlay = new GamePlayREPL(postLogin.getCurrentGameData().gameID());
                        gamePlay.play(postLogin.getCurrentGameData(), postLogin.getWhitePerspective(), postLogin.getObserver(), authToken);
                    } catch (ResponseException e) {
                        System.out.println(e.getMessage());
                    }
                }
                loggedIn = postLogin.getLoggedIn();
            }
        }
    }
}