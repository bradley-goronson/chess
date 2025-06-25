package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
        int row = position.getRow();
        int column = position.getColumn();

        int[][] positionAdjustments = {{1, 0}, {1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 0}, {-1, 1}, {-1, -1}};

        for (int[] adjustment : positionAdjustments) {
            ChessPosition candidatePosition = new ChessPosition(row + adjustment[0], column + adjustment[1]);
            if (candidatePosition.getRow() >= 1 && candidatePosition.getRow() <= 8 && candidatePosition.getColumn() >= 1 && candidatePosition.getColumn() <= 8 && (board.getPiece(candidatePosition) == null || board.getPiece(candidatePosition).getTeamColor() != currentColor)) {
                validMoves.add(new ChessMove(position, candidatePosition, null));
            }
        }
        return validMoves;
    }
}
