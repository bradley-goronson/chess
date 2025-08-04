package ui;

import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class GamePlayREPL {
    GameData currentGame = null;
    public void play(GameData currentGame, boolean whitePerspective) {
        printBoard(currentGame, whitePerspective);
    }

    private void printBoard(GameData gameData, boolean whitePerspective) {
        PrintStream output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] whiteEdge = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        String[] blackEdge = {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
        if (whitePerspective) {
            printEdge(output, whiteEdge);
        } else {
            printEdge(output, blackEdge);
        }
        ChessPiece[][] currentChessBoard = gameData.game().getBoard().board;
        for (int i = 0; i < currentChessBoard.length; i++) {
            output.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            if (whitePerspective) {
                output.print(" " + (8 - i) + " ");
            } else {
                output.print(" " + (i + 1) + " ");
            }
            String evenColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
            String oddColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
            if (i % 2 != 0) {
                evenColor = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                oddColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
            }

            ChessPiece[] currentRow = currentChessBoard[7 - i];
            if (!whitePerspective) {
                currentRow = currentChessBoard[i];
            }
            for (int j = 0; j < currentRow.length; j++) {
                if (j % 2 == 0) {
                    output.print(evenColor);
                } else {
                    output.print(oddColor);
                }
                ChessPiece tile = currentRow[j];
                output.print(" ");
                if (tile == null) {
                    output.print(" ");
                } else {
                    if (tile.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                        output.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.SET_TEXT_COLOR_WHITE);
//                        switch (tile.getPieceType()) {
//                            case KING -> output.print(EscapeSequences.WHITE_KING);
//                            case QUEEN -> output.print(EscapeSequences.WHITE_QUEEN);
//                            case BISHOP -> output.print(EscapeSequences.WHITE_BISHOP);
//                            case KNIGHT -> output.print(EscapeSequences.WHITE_KNIGHT);
//                            case ROOK -> output.print(EscapeSequences.WHITE_ROOK);
//                            case PAWN -> output.print(EscapeSequences.WHITE_PAWN);
//                        }
                    } else {
                        output.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_BLACK);
//                        switch (tile.getPieceType()) {
//                            case KING -> output.print(EscapeSequences.BLACK_KING);
//                            case QUEEN -> output.print(EscapeSequences.BLACK_QUEEN);
//                            case BISHOP -> output.print(EscapeSequences.BLACK_BISHOP);
//                            case KNIGHT -> output.print(EscapeSequences.BLACK_KNIGHT);
//                            case ROOK -> output.print(EscapeSequences.BLACK_ROOK);
//                            case PAWN -> output.print(EscapeSequences.BLACK_PAWN);
//                        }
                    }
                    switch (tile.getPieceType()) {
                        case KING -> output.print("K");
                        case QUEEN -> output.print("Q");
                        case BISHOP -> output.print("B");
                        case KNIGHT -> output.print("N");
                        case ROOK -> output.print("R");
                        case PAWN -> output.print("P");
                    }
                }
                output.print(" ");
            }
            output.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            if (whitePerspective) {
                output.print(" " + (8 - i) + " ");
            } else {
                output.print(" " + (i + 1) + " ");
            }            output.print(EscapeSequences.SET_BG_COLOR_BLACK);
            output.print("\n");
        }
        if (whitePerspective) {
            printEdge(output, whiteEdge);
        } else {
            printEdge(output, blackEdge);
        }
        System.out.println(EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.SET_TEXT_COLOR_WHITE + "\n");
    }

    private void printEdge(PrintStream output, String[] row) {
        output.print(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_BG_COLOR_DARK_GREY);
        for (String letter : row) {
            output.print(" ");
            output.print(EscapeSequences.SET_TEXT_COLOR_YELLOW);
            output.print(letter);
            output.print(" ");
        }
        output.print(EscapeSequences.SET_BG_COLOR_BLACK);
        output.print("\n");
    }
}
