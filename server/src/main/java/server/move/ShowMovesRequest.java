package server.move;

import chess.ChessPosition;
import server.joingame.UpdateGameRequest;

public class ShowMovesRequest extends UpdateGameRequest {
    private ChessPosition startPosition;

    public ChessPosition getStartPosition() {
        return startPosition;
    }
}
