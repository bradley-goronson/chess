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

        ChessPosition up = new ChessPosition(row + 1, column);
        ChessPosition upRight = new ChessPosition(row + 1, column + 1);
        ChessPosition upLeft = new ChessPosition(row + 1, column - 1);
        ChessPosition right = new ChessPosition(row, column + 1);
        ChessPosition left = new ChessPosition(row, column - 1);
        ChessPosition down = new ChessPosition(row - 1, column);
        ChessPosition downRight = new ChessPosition(row - 1, column + 1);
        ChessPosition downLeft = new ChessPosition(row - 1, column - 1);

        if (row != 8) {
            if (board.getPiece(up) == null || board.getPiece(up).getTeamColor() != currentColor) {
                validMoves.add(new ChessMove(position, up, null));
            }
            if (column != 1) {
                if (board.getPiece(upLeft) == null || board.getPiece(upLeft).getTeamColor() != currentColor) {
                    validMoves.add(new ChessMove(position, upLeft, null));
                }
            }
            if (column != 8) {
                if (board.getPiece(upRight) == null || board.getPiece(upRight).getTeamColor() != currentColor) {
                    validMoves.add(new ChessMove(position, upRight, null));
                }
            }
        }
        if (row != 1) {
            if (board.getPiece(down) == null || board.getPiece(down).getTeamColor() != currentColor) {
                validMoves.add(new ChessMove(position, down, null));
            }
            if (column != 1) {
                if (board.getPiece(downLeft) == null || board.getPiece(downLeft).getTeamColor() != currentColor) {
                    validMoves.add(new ChessMove(position, downLeft, null));
                }
            }
            if (column != 8) {
                if (board.getPiece(downRight) == null || board.getPiece(downRight).getTeamColor() != currentColor) {
                    validMoves.add(new ChessMove(position, downRight, null));
                }
            }
        }
        if (column != 1) {
            if (board.getPiece(left) == null || board.getPiece(left).getTeamColor() != currentColor) {
                validMoves.add(new ChessMove(position, left, null));
            }
        }
        if (column != 8) {
            if (board.getPiece(right) == null || board.getPiece(right).getTeamColor() != currentColor) {
                validMoves.add(new ChessMove(position, right, null));
            }
        }
            return validMoves;
        }
    }
