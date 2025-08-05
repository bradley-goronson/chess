package server;

import chess.ChessPosition;

public record ClientMakeMoveRequest (String gameID, ChessPosition startPosition, ChessPosition endPosition){}
