package chess;

import java.util.Collection;

public interface PieceMoveCalculator {
    /** @return a collection of valid ChessMoves given the board and a piece's position */
    Collection<ChessMove> getPieceMoves();
}
