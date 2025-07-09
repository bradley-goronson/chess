package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    public ChessPiece[][] board = new ChessPiece[8][8];
    CastlingType castlingType = null;

    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i <= 8; i++) {
            this.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        ChessPiece.PieceType[] nonPawnPieces = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING
        };

        for (int i = 0; i < 5; i++) {
            this.addPiece(new ChessPosition(1, 1 + i), new ChessPiece(ChessGame.TeamColor.WHITE, nonPawnPieces[i]));
            this.addPiece(new ChessPosition(8, 1 + i), new ChessPiece(ChessGame.TeamColor.BLACK, nonPawnPieces[i]));
            if (i < 3) {
                this.addPiece(new ChessPosition(1, 8 - i), new ChessPiece(ChessGame.TeamColor.WHITE, nonPawnPieces[i]));
                this.addPiece(new ChessPosition(8, 8 - i), new ChessPiece(ChessGame.TeamColor.BLACK, nonPawnPieces[i]));
            }
        }
    }

    public enum CastlingType {
        BlackLeft,
        BlackRight,
        WhiteLeft,
        WhiteRight,
    }

    public void setCastlingType(CastlingType castlingType) {
        this.castlingType = castlingType;
    }

    public CastlingType getCastlingType() {
        return this.castlingType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clonedChessBoard = (ChessBoard) super.clone();
            ChessPiece[][] clonedBoardArray = new ChessPiece[8][8];
            for (int i = 0; i < clonedChessBoard.board.length; i++) {
                for (int j = 0; j < clonedChessBoard.board[i].length; j++) {
                    ChessPiece currentPiece = clonedChessBoard.board[i][j];
                    if (currentPiece != null) {
                        clonedBoardArray[i][j] = currentPiece.clone();
                    } else {
                        clonedBoardArray[i][j] = null;
                    }
                }
            }
            clonedChessBoard.board = clonedBoardArray;
            return clonedChessBoard;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}