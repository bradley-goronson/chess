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
        ChessPiece.PieceType[] promotionPieces = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT};

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
        for (int i = 0; i < 4; i++) {
            int candidateRow = candidatePositions.get(i).getRow();
            int candidateColumn = candidatePositions.get(i).getColumn();
            if (candidateRow >= 1 && candidateRow <= 8 && candidateColumn >= 1 && candidateColumn <= 8) {
                if ((i == 0 && board.getPiece(candidatePositions.get(i)) == null) || (i > 1 && board.getPiece(candidatePositions.get(i)) != null && board.getPiece(candidatePositions.get(i)).getTeamColor() != currentColor)) {
                    if ((candidateRow == 8 && currentColor == ChessGame.TeamColor.WHITE) || (candidateRow == 1 && currentColor == ChessGame.TeamColor.BLACK)) {
                        for (ChessPiece.PieceType promotionPiece : promotionPieces) {
                            validMoves.add(new ChessMove(position, candidatePositions.get(i), promotionPiece));
                        }
                    } else {
                        validMoves.add(new ChessMove(position, candidatePositions.get(i), null));
                        if (i == 0) {
                            advanceOneValid = true;
                        }
                    }
                }
                if (i == 1 && advanceOneValid && ((currentRow == 2 && currentColor == ChessGame.TeamColor.WHITE) || (currentRow == 7 && currentColor == ChessGame.TeamColor.BLACK))) {
                    if (board.getPiece(candidatePositions.get(i)) == null) {
                        validMoves.add(new ChessMove(position, candidatePositions.get(i), null));
                    }
                }
            }
        }
        return validMoves;
    }
}
