package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator{
    @Override
    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int tilesFromEnd = position.getRow() - 8;
        int row = position.getRow();
        int column = position.getColumn();

        validMoves.add(new ChessMove(position, new ChessPosition(row + 1, column), null));

        return validMoves;
    }
}
