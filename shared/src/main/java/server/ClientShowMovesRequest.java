package server;

import chess.ChessPosition;

public record ClientShowMovesRequest(int gameID, ChessPosition startPosition){}
