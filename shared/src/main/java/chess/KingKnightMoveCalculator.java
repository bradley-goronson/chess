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

        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            ChessPosition leftRookPosition = new ChessPosition(position.getRow(), 1);
            ChessPosition rightRookPosition = new ChessPosition(position.getRow(), 8);

            if (castlingIsValid(board, position, leftRookPosition)) {
                validMoves.add(new ChessMove(position, new ChessPosition(position.getRow(), position.getColumn() - 2), null));
            }
            if (castlingIsValid(board, position, rightRookPosition)) {
                validMoves.add(new ChessMove(position, new ChessPosition(position.getRow(), position.getColumn() + 2), null));
            }
        }
        return validMoves;
    }

    public boolean castlingIsValid(ChessBoard board, ChessPosition kingPosition, ChessPosition rookPosition) {
        ChessPiece king = board.getPiece(kingPosition);
        ChessPiece rook = board.getPiece(rookPosition);
        if (king == null || king.getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        }
        if (king.getTeamColor() == ChessGame.TeamColor.WHITE && kingPosition.getRow() != 1) {
            return false;
        }
        if (king.getTeamColor() == ChessGame.TeamColor.BLACK && kingPosition.getRow() != 8) {
            return false;
        }
        if (kingPosition.getColumn() != 5) {
            return false;
        }
        if (king.hasMoved) {
            return false;
        }
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }
        if (rook.getTeamColor() == ChessGame.TeamColor.WHITE && rookPosition.getRow() != 1) {
            return false;
        }
        if (rook.getTeamColor() == ChessGame.TeamColor.BLACK && rookPosition.getRow() != 8) {
            return false;
        }
        if (rook.hasMoved) {
            return false;
        }

        Collection<ChessPosition> middlePositions = new ArrayList<>();
        if (rookPosition.getColumn() == 1) {
            middlePositions.add(new ChessPosition(kingPosition.getRow(), 2));
            middlePositions.add(new ChessPosition(kingPosition.getRow(), 3));
            middlePositions.add(new ChessPosition(kingPosition.getRow(), 4));
        } else {
            middlePositions.add(new ChessPosition(kingPosition.getRow(), 6));
            middlePositions.add(new ChessPosition(kingPosition.getRow(), 7));
        }
        for (ChessPosition position : middlePositions) {
            if (board.getPiece(position) != null) {
                return false;
            }
        }

//        Collection<ChessPosition> opponentPositions;
//        if (king.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            opponentPositions = ownedPositions(board, ChessGame.TeamColor.BLACK);
//        } else {
//            opponentPositions = ownedPositions(board, ChessGame.TeamColor.WHITE);
//        }
//        Collection<ChessMove> opponentMoves = new ArrayList<>();
//        for (ChessPosition position : opponentPositions) {
//            opponentMoves.addAll(getPieceMoves(board, position));
//        }
//
//        for (ChessPosition position : middlePositions) {
//            for (ChessMove move : opponentMoves) {
//                if (move.getEndPosition() == position) {
//                    return false;
//                }
//            }
//        }
        return true;
    }

    public Collection<ChessPosition> ownedPositions(ChessBoard board, ChessGame.TeamColor teamColor) {
        Collection<ChessPosition> currentTeamPositions = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece currentPiece = board.board[i][j];
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    currentTeamPositions.add(new ChessPosition(i + 1, j + 1));
                }
            }
        }
        return currentTeamPositions;
    }
}
