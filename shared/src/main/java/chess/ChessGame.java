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
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(currentBoard, startPosition);
        for (ChessMove move : possibleMoves) {
            ChessBoard savedBoard = currentBoard;
            TeamColor savedTeam = currentTeam;
            currentBoard = currentBoard.clone();
            try {
                makeMove(move);
                validMoves.add(move);
            } catch (InvalidMoveException e) {
                continue;
            } finally {
                setBoard(savedBoard);
                setTeamTurn(savedTeam);
            }
        }
        return validMoves;
    }
    
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //we want to update the currentBoard so the piece specified in the provided ChessMove is now at the end position.
        //that involves setting the entry of the ChessPiece[][] board array that corresponds with the startPosition of the
        //ChessMove to be null and setting the entry at the end position to be the piece that was at startPosition. We can update these
        //entries using these positions with the ChessBoard's addPiece() method.
        ChessPiece movingPiece = currentBoard.getPiece(move.getStartPosition());
        currentBoard.addPiece(move.getEndPosition(), movingPiece);
        currentBoard.addPiece(move.getStartPosition(), null);
        if (isInCheck(currentTeam)) {
            throw new InvalidMoveException();
        }
        if (currentTeam == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
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
                if (opponentMove.getEndPosition() == kingPosition) {
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




