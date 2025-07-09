package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingKnightMoveCalculator implements PieceMoveCalculator {
    int[][] positionAdjustments;

    public KingKnightMoveCalculator(int[][] positionAdjustments) {
        this.positionAdjustments = positionAdjustments;
    }

    @Override
    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
        int row = position.getRow();
        int column = position.getColumn();

        for (int[] adjustment : positionAdjustments) {
            ChessPosition candidatePosition = new ChessPosition(row + adjustment[0], column + adjustment[1]);
            boolean outOfBounds = true;
            int candidateRow = candidatePosition.getRow();
            int candidateColumn = candidatePosition.getColumn();
            if (candidateRow >= 1 && candidateRow <= 8 && candidateColumn >= 1 && candidateColumn <= 8) {
                outOfBounds = false;
            }

            if (!outOfBounds && (board.getPiece(candidatePosition) == null || board.getPiece(candidatePosition).getTeamColor() != currentColor)) {
                validMoves.add(new ChessMove(position, candidatePosition, null));
            }
        }
        return validMoves;
    }
}
