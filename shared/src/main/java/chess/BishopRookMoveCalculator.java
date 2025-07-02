package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopRookMoveCalculator implements PieceMoveCalculator {
    int[][] positionAdjustments;

    public BishopRookMoveCalculator(int[][] positionAdjustments) {
        this.positionAdjustments = positionAdjustments;
    }

    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();

        for (int[] adjustment : positionAdjustments) {
            boolean foundOtherPiece = false;
            int row = position.getRow();
            int column = position.getColumn();
            int nextRow = row + adjustment[0];
            int nextColumn = column + adjustment[1];

            while (!foundOtherPiece && nextRow >= 1 && nextRow <= 8 && nextColumn >= 1 && nextColumn <= 8) {
                ChessPosition candidatePosition = new ChessPosition(nextRow, nextColumn);
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
                nextRow = row + adjustment[0];
                nextColumn = column + adjustment[1];
            }
        }
        return validMoves;
    }
}
