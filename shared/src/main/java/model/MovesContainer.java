package model;

import chess.ChessMove;

import java.util.Collection;

public record MovesContainer(Collection<ChessMove> validMoves) {
}
