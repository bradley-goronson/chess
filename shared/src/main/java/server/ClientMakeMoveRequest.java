package server;

import chess.ChessMove;
import chess.ChessPosition;

public record ClientMakeMoveRequest (int gameID, ChessMove move){}
