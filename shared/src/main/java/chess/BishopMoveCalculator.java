package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();

        int[][] positionAdjustments = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] adjustment : positionAdjustments) {
            boolean foundOtherPiece = false;
            int row = position.getRow();
            int column = position.getColumn();

            while (!foundOtherPiece && row + adjustment[0] >= 1 && row + adjustment[0] <= 8 && column + adjustment[1] >= 1 && column + adjustment[1] <= 8) {
                ChessPosition candidatePosition = new ChessPosition(row + adjustment[0], column + adjustment[1]);
                if (board.getPiece(candidatePosition) != null) {
                    if (board.getPiece(candidatePosition).getTeamColor() != currentColor) {
                        validMoves.add(new ChessMove(position, candidatePosition, null));
                    }
                    foundOtherPiece = true;
                } else {
                    validMoves.add(new ChessMove(position, candidatePosition, null));
                }
                row += adjustment[0];
                column += adjustment[1];
            }
        }
        return validMoves;
    }
}
