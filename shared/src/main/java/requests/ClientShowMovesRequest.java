package requests;

import chess.ChessPosition;

public record ClientShowMovesRequest(int gameID, ChessPosition startPosition){}
