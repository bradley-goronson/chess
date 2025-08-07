package server.move;

import chess.ChessMove;
import server.joingame.UpdateGameRequest;

public class MakeMoveRequest extends UpdateGameRequest {
    private final ChessMove move = null;

    public ChessMove getMove() {
        return move;
    }
}
