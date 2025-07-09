package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {
    public ChessGame.TeamColor pieceColor;
    public PieceType type;
    public boolean hasMoved = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        if (this.type == PieceType.PAWN) {
            PieceMoveCalculator pawnMoveCalculator = new PawnMoveCalculator();
            validMoves = pawnMoveCalculator.getPieceMoves(board, myPosition);
        }
        if (this.type == PieceType.KING) {
            int[][] positionAdjustments = {{1, 0}, {1, 1}, {1, -1}, {0, 1}, {0, -1}, {-1, 0}, {-1, 1}, {-1, -1}};
            PieceMoveCalculator kingMoveCalculator = new KingKnightMoveCalculator(positionAdjustments);
            validMoves = kingMoveCalculator.getPieceMoves(board, myPosition);
        }
        if (this.type == PieceType.KNIGHT) {
            int[][] positionAdjustments = {{2, -1}, {2, 1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {1, 2}, {-1, 2}};
            PieceMoveCalculator knightMoveCalculator = new KingKnightMoveCalculator(positionAdjustments);
            validMoves = knightMoveCalculator.getPieceMoves(board, myPosition);
        }
        if (this.type == PieceType.ROOK) {
            int[][] positionAdjustments = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
            PieceMoveCalculator rookMoveCalculator = new BishopRookMoveCalculator(positionAdjustments);
            validMoves = rookMoveCalculator.getPieceMoves(board, myPosition);
        }
        if (this.type == PieceType.BISHOP) {
            int[][] positionAdjustments = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            PieceMoveCalculator bishopMoveCalculator = new BishopRookMoveCalculator(positionAdjustments);
            validMoves = bishopMoveCalculator.getPieceMoves(board, myPosition);
        }
        if (this.type == PieceType.QUEEN) {
            int[][] rookPositionAdjustments = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
            int[][] bishopPositionAdjustments = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            PieceMoveCalculator rookMoveCalculator = new BishopRookMoveCalculator(rookPositionAdjustments);
            PieceMoveCalculator bishopMoveCalculator = new BishopRookMoveCalculator(bishopPositionAdjustments);

            validMoves.addAll(rookMoveCalculator.getPieceMoves(board, myPosition));
            validMoves.addAll(bishopMoveCalculator.getPieceMoves(board, myPosition));
        }
        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
