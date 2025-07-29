import server.ResponseException;
import ui.EscapeSequences;
import ui.GamePlayUI;
import ui.PostLoginREPL;
import ui.PreLoginREPL;

public class Main {
    public static void main(String[] args) throws ResponseException {
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
                    GamePlayUI gamePlay = new GamePlayUI();
                    gamePlay.play();
                }
                loggedIn = postLogin.getLoggedIn();
            }
        }
    }
}