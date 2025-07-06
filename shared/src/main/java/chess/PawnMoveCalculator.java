package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator {
    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
        int currentRow = position.getRow();
        int currentColumn = position.getColumn();
        ChessPiece.PieceType[] promotionPieces = {
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT
        };

        int[][] positionAdjustments = {{1, 0}, {2, 0}, {1, 1}, {1, -1}};
        ArrayList<ChessPosition> candidatePositions = new ArrayList<>();

        for (int[] adjustment : positionAdjustments) {
            if (currentColor == ChessGame.TeamColor.WHITE) {
                candidatePositions.add(new ChessPosition(currentRow + adjustment[0], currentColumn + adjustment[1]));
            } else {
                candidatePositions.add(new ChessPosition(currentRow - adjustment[0], currentColumn + adjustment[1]));
            }
        }

        boolean advanceOneValid = false;
        int loop = 1;
        for (ChessPosition candidatePosition : candidatePositions) {
            int candidateRow = candidatePosition.getRow();
            int candidateColumn = candidatePosition.getColumn();
            boolean outOfBounds = true;

            if (candidateRow >= 1 && candidateRow <= 8 && candidateColumn >= 1 && candidateColumn <= 8) {
                outOfBounds = false;
            }

            if (loop == 1 && !outOfBounds && board.getPiece(candidatePosition) == null) {
                if (candidateRow == 8 || candidateRow == 1) {
                    for (ChessPiece.PieceType promotionPiece : promotionPieces) {
                        validMoves.add(new ChessMove(position, candidatePosition, promotionPiece));
                    }
                } else {
                    validMoves.add(new ChessMove(position, candidatePosition, null));
                }
                advanceOneValid = true;
            }

            if ((currentRow == 2 && currentColor == ChessGame.TeamColor.WHITE) || (currentRow == 7 && currentColor == ChessGame.TeamColor.BLACK)) {
                if (loop == 2 && advanceOneValid && !outOfBounds && board.getPiece(candidatePosition) == null) {
                    validMoves.add(new ChessMove(position, candidatePosition, null));
                }
            }

            if (loop > 2 && !outOfBounds && board.getPiece(candidatePosition) != null && board.getPiece(candidatePosition).getTeamColor() != currentColor) {
                if (candidateRow == 8 || candidateRow == 1) {
                    for (ChessPiece.PieceType promotionPiece : promotionPieces) {
                        validMoves.add(new ChessMove(position, candidatePosition, promotionPiece));
                    }
                } else {
                    validMoves.add(new ChessMove(position, candidatePosition, null));
                }
            }
            loop++;
        }
        return validMoves;
    }
}
