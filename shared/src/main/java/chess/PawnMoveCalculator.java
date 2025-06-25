package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator{
    @Override
    public Collection<ChessMove> getPieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);
        ChessGame.TeamColor currentColor = currentPiece.getTeamColor();
        int row = position.getRow();
        int column = position.getColumn();

        if (currentColor == ChessGame.TeamColor.WHITE) {
            ChessPosition advanceOnePosition = new ChessPosition(row + 1, column);
            ChessPosition advanceTwoPosition = new ChessPosition(row + 2, column);
            ChessPosition captureLeft = new ChessPosition(row + 1, column - 1);
            ChessPosition captureRight = new ChessPosition(row + 1, column + 1);

            if (row == 2 && board.getPiece(advanceTwoPosition) == null) {
                validMoves.add(new ChessMove(position, advanceTwoPosition, null));
            }
            if (board.getPiece(advanceOnePosition) == null) {
                if (row == 7) {
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, advanceOnePosition, null));
                }
            }
            if (column != 1 && board.getPiece(captureLeft) != null && board.getPiece(captureLeft).getTeamColor() != ChessGame.TeamColor.WHITE) {
                if (row == 7) {
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, captureLeft, null));
                }
            }
            if (column != 8 && board.getPiece(captureRight) != null && board.getPiece(captureRight).getTeamColor() != ChessGame.TeamColor.WHITE) {
                if (row == 7) {
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, captureRight, null));
                }
            }
        }

        if (currentColor == ChessGame.TeamColor.BLACK) {
            ChessPosition advanceOnePosition = new ChessPosition(row - 1, column);
            ChessPosition advanceTwoPosition = new ChessPosition(row - 2, column);
            ChessPosition captureLeft = new ChessPosition(row - 1, column - 1);
            ChessPosition captureRight = new ChessPosition(row - 1, column + 1);

            if (row == 7 && board.getPiece(advanceOnePosition) == null && board.getPiece(advanceTwoPosition) == null) {
                validMoves.add(new ChessMove(position, advanceTwoPosition, null));
            }
            if (board.getPiece(advanceOnePosition) == null) {
                if (row == 2) {
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, advanceOnePosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, advanceOnePosition, null));
                }
            }
            if (column != 1 && board.getPiece(captureLeft) != null && board.getPiece(captureLeft).getTeamColor() != ChessGame.TeamColor.BLACK) {
                if (row == 2) {
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, captureLeft, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, captureLeft, null));
                }
            }
            if (column != 8 && board.getPiece(captureRight) != null && board.getPiece(captureRight).getTeamColor() != ChessGame.TeamColor.BLACK) {
                if (row == 2) {
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, captureRight, ChessPiece.PieceType.KNIGHT));
                } else {
                    validMoves.add(new ChessMove(position, captureRight, null));
                }
            }
        }

        return validMoves;
    }
}
