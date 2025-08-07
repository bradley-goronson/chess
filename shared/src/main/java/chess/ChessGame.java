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
    boolean gameOver = false;

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

    public void setGameOver(boolean status) {gameOver = status;}

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
        if (piece == null) {
            return null;
        }
        alternateUniverse = true;
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
    public ChessGame makeMove(ChessMove move, TeamColor playerColor) throws InvalidMoveException {
        ChessPiece movingPiece = currentBoard.getPiece(move.getStartPosition());
        ChessPiece destinationPiece = currentBoard.getPiece(move.getEndPosition());
        if (movingPiece == null) {
            throw new InvalidMoveException("Error: You can't move without a piece!");
        }

        if (!alternateUniverse) {
            if (movingPiece.getTeamColor() != getTeamTurn()) {
                throw new InvalidMoveException("Error: You can't move an opponent's piece!");
            }
        }
        if (destinationPiece != null && destinationPiece.getTeamColor() == movingPiece.getTeamColor()) {
            throw new InvalidMoveException("Error: You can't capture one of your own pieces!");
        }

        Collection<ChessMove> possibleMoves = movingPiece.pieceMoves(currentBoard, move.getStartPosition());
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException("Error: That isn't a valid move");
        }

        if (move.getPromotionPiece() != null) {
            movingPiece.type = move.getPromotionPiece();
        }

        currentBoard.addPiece(move.getEndPosition(), movingPiece);
        currentBoard.addPiece(move.getStartPosition(), null);

        if (isInCheck(movingPiece.getTeamColor())) {
            throw new InvalidMoveException("Error: That move puts you in check!");
        }

        if (!alternateUniverse) {
            if (currentTeam == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        }
        return this;
    }

    public ChessGame makeMove(ChessMove move) throws InvalidMoveException{
        return makeMove(move, TeamColor.WHITE);
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
            return noValidMoves(teamColor);
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
            return noValidMoves(teamColor);
        }
        return false;
    }

    public boolean noValidMoves(TeamColor teamColor) {
        Collection<ChessPosition> currentTeamPositions = ownedPositions(teamColor);
        for (ChessPosition position : currentTeamPositions) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
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




