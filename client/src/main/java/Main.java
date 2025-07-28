import chess.*;
import ui.GamePlayUI;
import ui.PostLoginREPL;
import ui.PreLoginREPL;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        String authToken;

        PreLoginREPL preLogin = new PreLoginREPL();
        boolean loggedIn = preLogin.repl();

        if (loggedIn) {
            authToken = preLogin.getAuthToken();
            PostLoginREPL postLogin = new PostLoginREPL();
            boolean joinedGame = postLogin.repl(authToken);

            if (joinedGame) {
                GamePlayUI gamePlay = new GamePlayUI();
                gamePlay.play();
            }
        }
    }
}