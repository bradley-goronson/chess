package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard currentBoard = new ChessBoard();
    TeamColor currentTeam = TeamColor.WHITE;
    boolean alternateUniverse = false;

    public ChessGame() {
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = currentBoard.getPiece(startPosition);
        alternateUniverse = true;
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(currentBoard, startPosition);
        for (ChessMove move : possibleMoves) {
            ChessBoard savedBoard = currentBoard;
            currentBoard = currentBoard.clone();
            try {
                makeMove(move);
                validMoves.add(move);
            } catch (InvalidMoveException e) {
                continue;
            } finally {
                setBoard(savedBoard);
            }
        }
        alternateUniverse = false;
        return validMoves;
    }
    
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = currentBoard.getPiece(move.getStartPosition());
        ChessPiece destinationPiece = currentBoard.getPiece(move.getEndPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException();
        }
        if (!alternateUniverse) {
            if (movingPiece.getTeamColor() != getTeamTurn()) {
                throw new InvalidMoveException();
            }
        }
        if (destinationPiece != null && destinationPiece.getTeamColor() == movingPiece.getTeamColor()) {
            throw new InvalidMoveException();
        }

        if (move.getPromotionPiece() != null) {
            movingPiece.type = move.getPromotionPiece();
        }

        Collection<ChessMove> possibleMoves = movingPiece.pieceMoves(currentBoard, move.getStartPosition());
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        if (movingPiece.type == ChessPiece.PieceType.KING && Math.abs(move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) == 2) {
            if (move.getEndPosition().getColumn() == 3 && !kingSideCastleValid(move.getStartPosition())) {
                throw new InvalidMoveException();
            }
            if (move.getEndPosition().getColumn() == 7 && !queenSideCastleValid(move.getStartPosition())) {
                throw new InvalidMoveException();
            }
            considerCastling(move);
        }

        currentBoard.addPiece(move.getEndPosition(), movingPiece);
        currentBoard.addPiece(move.getStartPosition(), null);

        if (isInCheck(movingPiece.getTeamColor())) {
            throw new InvalidMoveException();
        }

        if (!alternateUniverse) {
            currentBoard.setCastlingType(null);
            movingPiece.hasMoved = true;
            if (movingPiece.getTeamColor() == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> opponentPositions = new ArrayList<>();
        ChessPosition kingPosition = new ChessPosition(0, 0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece currentPiece = currentBoard.board[i][j];
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    if (currentPiece.type == ChessPiece.PieceType.KING) {
                        kingPosition = new ChessPosition(i + 1, j + 1);
                    }
                } else {
                    if (currentPiece != null) {
                        opponentPositions.add(new ChessPosition(i + 1, j + 1));
                    }
                }
            }
        }
        for (ChessPosition opponentPosition : opponentPositions) {
            ChessPiece opponentPiece = currentBoard.getPiece(opponentPosition);
            Collection<ChessMove> opponentMoves = opponentPiece.pieceMoves(currentBoard, opponentPosition);
            for (ChessMove opponentMove : opponentMoves) {
                if (opponentMove.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            Collection<ChessPosition> currentTeamPositions = ownedPositions(teamColor);
            for (ChessPosition position : currentTeamPositions) {
                if (!validMoves(position).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            Collection<ChessPosition> currentTeamPositions = ownedPositions(teamColor);
            for (ChessPosition position : currentTeamPositions) {
                if (!validMoves(position).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    public Collection<ChessPosition> ownedPositions(TeamColor teamColor) {
        Collection<ChessPosition> currentTeamPositions = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece currentPiece = currentBoard.board[i][j];
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    currentTeamPositions.add(new ChessPosition(i + 1, j + 1));
                }
            }
        }
        return currentTeamPositions;
    }

    public boolean kingSideCastleValid(ChessPosition kingPosition) {
        alternateUniverse = true;
        ChessMove middlePositionMove = new ChessMove(new ChessPosition(kingPosition.getRow(), kingPosition.getColumn()), new ChessPosition(kingPosition.getRow(), 4), null);
        ChessBoard savedBoard = currentBoard;
        currentBoard = currentBoard.clone();
        try {
            makeMove(middlePositionMove);
        } catch (InvalidMoveException e) {
            return false;
        } finally {
            setBoard(savedBoard);
        }
        return true;
    }

    public boolean queenSideCastleValid(ChessPosition kingPosition) {
        alternateUniverse = true;
        ChessMove middlePositionMove = new ChessMove(new ChessPosition(kingPosition.getRow(), kingPosition.getColumn()), new ChessPosition(kingPosition.getRow(), 6), null);
        ChessBoard savedBoard = currentBoard;
        currentBoard = currentBoard.clone();
        try {
            makeMove(middlePositionMove);
        } catch (InvalidMoveException e) {
            return false;
        } finally {
            setBoard(savedBoard);
        }
        return true;
    }

    public void considerCastling(ChessMove move) throws InvalidMoveException {
        int row = move.getStartPosition().getRow();
        int startingColumn = move.getStartPosition().getColumn();
        int endingColumn = move.getEndPosition().getColumn();
        ChessPiece movingPiece = currentBoard.getPiece(move.getStartPosition());
        if (movingPiece == null) {
            return;
        }

        if (Math.abs(endingColumn - startingColumn) == 2) {
            if (endingColumn == 3) {
                if (currentTeam == TeamColor.BLACK) {
                    currentBoard.setCastlingType(ChessBoard.CastlingType.BlackLeft);
                } else {
                    currentBoard.setCastlingType(ChessBoard.CastlingType.WhiteLeft);
                }
                makeMove(new ChessMove(new ChessPosition(row, 1), new ChessPosition(row, 4), null));
            }
            if (endingColumn == 7) {
                if (currentTeam == TeamColor.BLACK) {
                    currentBoard.setCastlingType(ChessBoard.CastlingType.BlackRight);
                } else {
                    currentBoard.setCastlingType(ChessBoard.CastlingType.WhiteRight);
                }
                makeMove(new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6), null));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return Objects.equals(currentBoard, chessGame.currentBoard) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentBoard, currentTeam);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "currentBoard=" + currentBoard +
                ", currentTeam=" + currentTeam +
                '}';
    }
}




